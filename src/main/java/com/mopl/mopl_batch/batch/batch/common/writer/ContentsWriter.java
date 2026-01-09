package com.mopl.mopl_batch.batch.batch.common.writer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.mopl.mopl_batch.batch.batch.metric.BatchMetricsService;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamWriter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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

	private int totalContentsWritten = 0;

	private static final String TOTAL_CONTENTS_KEY = "total.contents.written";

	@Override
	public void open(ExecutionContext executionContext) throws ItemStreamException {
		totalContentsWritten = executionContext.getInt(TOTAL_CONTENTS_KEY, 0);
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

		// Content 저장
		List<Content> contents = uniqueItems.stream()
			.map(item -> {
				return new Content(
					item.getTitle(),
					item.getDescription(),
					item.getType(),
					item.getThumbnailUrl(),
					item.getSourceId()
				);
			})
			.collect(Collectors.toList());
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
	}

}
