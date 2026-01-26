package com.mopl.mopl_batch.batch.batch.common.writer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.mopl.mopl_batch.batch.batch.metric.BatchMetricsService;
import com.mopl.mopl_batch.batch.storage.BinaryStorage;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamWriter;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import com.mopl.mopl_batch.batch.Repository.ContentRepository;
import com.mopl.mopl_batch.batch.Repository.ContentTagRepository;
import com.mopl.mopl_batch.batch.Repository.TagRepository;
import com.mopl.mopl_batch.batch.batch.common.dto.ContentFetchDto;
import com.mopl.mopl_batch.batch.entity.Content;
import com.mopl.mopl_batch.batch.entity.ContentTag;
import com.mopl.mopl_batch.batch.entity.Tag;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ContentsWriter implements ItemStreamWriter<ContentFetchDto> {

	private final ContentRepository contentRepository;
	private final ContentTagRepository contentTagRepository;
	private final TagRepository tagRepository;
	private final BatchMetricsService batchMetricsService;
	private final BinaryStorage binaryStorage;

	private final RestClient imageDownloadClient = RestClient.create();

	private ExecutorService imageExecutor;
	private int totalContentsWritten = 0;

	private static final String TOTAL_CONTENTS_KEY = "total.contents.written";
	private static final int THREAD_POOL_SIZE = 10;

	@Override
	public void open(ExecutionContext executionContext) throws ItemStreamException {
		totalContentsWritten = executionContext.getInt(TOTAL_CONTENTS_KEY, 0);
		imageExecutor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
	}

	@Override
	@Transactional
	public void write(Chunk<? extends ContentFetchDto> chunk) {
		if (chunk.isEmpty()) {
			return;
		}

		// 중복 제거.
		Map<Long, ContentFetchDto> contentMap = chunk.getItems().stream()
			.collect(Collectors.toMap(
				ContentFetchDto::getSourceId,
				item -> item,
				(existing, replacement) -> replacement
			));
		List<ContentFetchDto> uniqueItems = new ArrayList<>(contentMap.values());

		// Content 저장 (썸네일을 S3에 병렬 업로드 후 URL 저장)
		long uploadStartTime = System.currentTimeMillis();

		List<CompletableFuture<String>> futures = uniqueItems.stream()
			.map(item -> CompletableFuture.supplyAsync(() -> uploadThumbnailToS3(item), imageExecutor))
			.toList();

		List<String> s3Urls = futures.stream()
			.map(CompletableFuture::join)
			.toList();

		long uploadEndTime = System.currentTimeMillis();
		log.info("[ContentsWriter.write] thumbnail upload completed. count={}, totalTime={}ms",
			uniqueItems.size(), uploadEndTime - uploadStartTime);

		List<Content> contents = new ArrayList<>();
		for (int i = 0; i < uniqueItems.size(); i++) {
			ContentFetchDto item = uniqueItems.get(i);
			contents.add(new Content(
				item.getTitle(),
				item.getDescription(),
				item.getType(),
				s3Urls.get(i),
				item.getSourceId()
			));
		}
		List<Content> savedContents = contentRepository.saveAll(contents);

		batchMetricsService.incrementSavedCount(uniqueItems.getFirst().getType(), savedContents.size());

		Set<String> allTagNames = uniqueItems.stream()
			.flatMap(item -> item.getTags().stream())
			.collect(Collectors.toSet());

		Map<String, Tag> tagMap = getOrCreateTagsInBatch(allTagNames);

		List<ContentTag> contentTags = new ArrayList<>();
		for (int i = 0; i < savedContents.size(); i++) {
			Content savedContent = savedContents.get(i);
			List<String> tagNames = new ArrayList<>(uniqueItems.get(i).getTags());

			for (String tagName : tagNames) {
				Tag tag = tagMap.get(tagName);
				contentTags.add(new ContentTag(savedContent, tag));
			}
		}

		contentTagRepository.saveAll(contentTags);

		totalContentsWritten += savedContents.size();

		log.info("[ContentsWriter.write] chunk written. contents={}, totalContents={}",
			savedContents.size(), totalContentsWritten);
	}

	private String uploadThumbnailToS3(ContentFetchDto item) {
		String thumbnailUrl = item.getThumbnailUrl();

		if (thumbnailUrl == null || thumbnailUrl.isBlank()) {
			return null;
		}

		try {
			// 이미지 다운로드
			long downloadStart = System.currentTimeMillis();
			byte[] imageData = imageDownloadClient.get()
				.uri(thumbnailUrl)
				.retrieve()
				.body(byte[].class);
			long downloadTime = System.currentTimeMillis() - downloadStart;

			if (imageData == null || imageData.length == 0) {
				log.warn("[ContentsWriter.uploadThumbnailToS3] empty image data. type={}, sourceId={}, url={}",
					item.getType(), item.getSourceId(), thumbnailUrl);
				return null;
			}

			String contentType = detectContentType(thumbnailUrl);

			// S3 업로드
			long uploadStart = System.currentTimeMillis();
			String s3Url = binaryStorage.putThumbnail(
				item.getType(),
				item.getSourceId(),
				imageData,
				contentType
			);
			long uploadTime = System.currentTimeMillis() - uploadStart;

			log.debug("[ContentsWriter.uploadThumbnailToS3] completed. type={}, sourceId={}, downloadTime={}ms, uploadTime={}ms, size={}bytes",
				item.getType(), item.getSourceId(), downloadTime, uploadTime, imageData.length);

			return s3Url;
		} catch (Exception e) {
			log.warn("[ContentsWriter.uploadThumbnailToS3] failed to upload. type={}, sourceId={}, url={}, error={}",
				item.getType(), item.getSourceId(), thumbnailUrl, e.getMessage());
			return null;
		}
	}



	@Override
	public void update(ExecutionContext executionContext) throws ItemStreamException {
		executionContext.putInt(TOTAL_CONTENTS_KEY, totalContentsWritten);
		log.debug("[ContentsWriter.update] execution context updated. totalContents={}",
			totalContentsWritten);
	}

	@Override
	public void close() throws ItemStreamException {
		log.info("[ContentsWriter.close] writer closing. totalContents={}",
			totalContentsWritten);
		totalContentsWritten = 0;

		imageExecutor.shutdown();
		try {
			if (!imageExecutor.awaitTermination(60, TimeUnit.SECONDS)) {
				imageExecutor.shutdownNow();
			}
		} catch (InterruptedException e) {
			imageExecutor.shutdownNow();
			Thread.currentThread().interrupt();
		}
	}

	private String detectContentType(String url) {
		if (url == null) {
			return MediaType.IMAGE_JPEG_VALUE;
		}
		String lowerUrl = url.toLowerCase();
		if (lowerUrl.contains(".png")) {
			return MediaType.IMAGE_PNG_VALUE;
		} else if (lowerUrl.contains(".gif")) {
			return MediaType.IMAGE_GIF_VALUE;
		} else if (lowerUrl.contains(".webp")) {
			return "image/webp";
		}
		return MediaType.IMAGE_JPEG_VALUE;
	}

	private Map<String, Tag> getOrCreateTagsInBatch(Set<String> tagNames) {

		List<Tag> existingTags = tagRepository.findAllByNameIn(tagNames);

		Map<String, Tag> tagMap = existingTags.stream()
			.collect(Collectors.toMap(Tag::getName, tag -> tag));

		Set<String> newTagNames = tagNames.stream()
			.filter(name -> !tagMap.containsKey(name))
			.collect(Collectors.toSet());

		if (!newTagNames.isEmpty()) {
			List<Tag> newTags = new ArrayList<>();
			for (String name : newTagNames) {
				newTags.add(new Tag(name));
			}

			List<Tag> savedNewTags = tagRepository.saveAll(newTags);

			savedNewTags.forEach(tag -> tagMap.put(tag.getName(), tag));
		}

		return tagMap;
	}

}
