package com.mopl.mopl_batch.batch.batch.tmdb.writer;

import java.util.List;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamWriter;
import org.springframework.stereotype.Component;

import com.mopl.mopl_batch.batch.Repository.ContentRepository;
import com.mopl.mopl_batch.batch.batch.tmdb.dto.ContentSaveDto;
import com.mopl.mopl_batch.batch.entity.Content;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class TmdbMovieWriter implements ItemStreamWriter<ContentSaveDto> {

	private final ContentRepository contentRepository;

	private int totalWritten = 0;
	private static final String TOTAL_WRITTEN_KEY = "tmdb.movie.total.written";

	@Override
	public void write(Chunk<? extends ContentSaveDto> chunk) {
		if (chunk.isEmpty())
			return;

		List<Content> contents = chunk.getItems().stream().map(
			ContentSaveDto::of
		).toList();

		contentRepository.saveAll(contents);
		totalWritten += contents.size();
		log.info("Total written: {}", totalWritten);
	}

	@Override
	public void open(ExecutionContext executionContext) throws ItemStreamException {
		if (executionContext.containsKey(TOTAL_WRITTEN_KEY)) {
			totalWritten = executionContext.getInt(TOTAL_WRITTEN_KEY);
		} else {
			totalWritten = 0;
		}
	}

	@Override
	public void update(ExecutionContext ec) throws ItemStreamException {
		ec.putInt(TOTAL_WRITTEN_KEY, totalWritten);
	}

	@Override
	public void close() throws ItemStreamException {
		totalWritten = 0;
	}
}
