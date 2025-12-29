package com.mopl.mopl_batch.batch.batch.tmdb.reader;

import java.util.List;

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
@RequiredArgsConstructor
public class TmdbMovieReader implements ItemStreamReader<ContentSaveDto> {

	private final TmdbClient tmdbClient;
	private int currentPage;
	private int currentIndex;
	private List<ContentSaveDto> currentPageData;

	private static final int MAX_PAGES = 10;
	private static final String CURRENT_PAGE_KEY = "tmdb.movie.current.page";
	private static final String CURRENT_INDEX_KEY = "tmdb.movie.current.index";

	@Override
	public void open(ExecutionContext ec) throws ItemStreamException {
		if (ec.containsKey(CURRENT_PAGE_KEY)) {
			currentPage = ec.getInt(CURRENT_PAGE_KEY);
			currentIndex = ec.getInt(CURRENT_INDEX_KEY);
		} else {
			currentPage = 1;
			currentIndex = 0;
			currentPageData = null;
		}
	}

	@Override
	public ContentSaveDto read() {
		if (currentPageData == null || currentIndex >= currentPageData.size()) {
			currentPageData = tmdbClient.fetchContent(Type.MOVIE, currentPage);
			currentIndex = 0;
			currentPage++;
			log.info("currentPage: {}", currentPage);
			log.info("currentPageData: {}", currentPageData.size());
			if (currentPageData.isEmpty() || currentPage > MAX_PAGES) {
				return null;
			}
		}

		return currentPageData.get(currentIndex++);
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
