package com.mopl.mopl_batch.batch.batch.tmdb.reader;

import java.util.List;

import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.stereotype.Component;

import com.mopl.mopl_batch.batch.batch.tmdb.client.TmdbClient;
import com.mopl.mopl_batch.batch.batch.tmdb.dto.ContentSaveDto;
import com.mopl.mopl_batch.batch.entity.Type;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@StepScope
@RequiredArgsConstructor
public class TmdbReader implements ItemStreamReader<ContentSaveDto> {

	private final TmdbClient tmdbClient;

	private Type currentType;

	private int currentPage;
	private int currentIndex;
	private List<ContentSaveDto> currentPageData;

	private static final int MAX_PAGES = 100;
	private static final String CURRENT_PAGE_KEY = "tmdb.current.page";
	private static final String CURRENT_INDEX_KEY = "tmdb.current.index";

	public static final String CURRENT_TYPE_KEY = "tmdb.current.type";

	@BeforeStep
	public void beforeStep(StepExecution stepExecution) {
		ExecutionContext ec = stepExecution.getExecutionContext();
		this.currentType = (Type)ec.get(CURRENT_TYPE_KEY);
	}

	@Override
	public void open(ExecutionContext ec) throws ItemStreamException {
		this.currentPage = 1;
		this.currentIndex = 0;
		this.currentPageData = null;

		if (ec.containsKey(CURRENT_PAGE_KEY)) {
			currentPage = ec.getInt(CURRENT_PAGE_KEY);
		}
		if (ec.containsKey(CURRENT_INDEX_KEY)) {
			currentIndex = ec.getInt(CURRENT_INDEX_KEY);
		}
		log.info("[TmdbReader.open] currentPage: {}", currentPage);
	}

	@Override
	public ContentSaveDto read() {
		while (true) {
			if (currentType == null) {
				throw new IllegalStateException(
					"TmdbReader.currentType is null. StepExecutionContext에 type이 설정되어야 한다..");
			}

			if (currentPage > MAX_PAGES) {
				log.info("[TmdbReader.read] reach MAX_PAGES. type={}, page={}", currentType, currentPage);
				return null;
			}

			if (currentPageData == null) {
				currentPageData = tmdbClient.fetchContent(currentType, currentPage);

				if (currentPageData == null || currentPageData.isEmpty()) {
					log.info("[TmdbReader.read] empty page. type={}, page={}", currentType, currentPage);
					return null;
				}
			}

			if (currentIndex >= currentPageData.size()) {
				System.out.println(currentPage);
				currentPage++;
				currentIndex = 0;
				currentPageData = null;
				continue;
			}

			ContentSaveDto item = currentPageData.get(currentIndex++);
			return item;
		}
	}

	@Override
	public void update(ExecutionContext ec) throws ItemStreamException {
		ec.putInt(CURRENT_PAGE_KEY, currentPage);
		ec.putInt(CURRENT_INDEX_KEY, currentIndex);
	}

	@Override
	public void close() throws ItemStreamException {
		currentPageData = null;
		currentPage = 1;
		currentIndex = 0;
	}
}
