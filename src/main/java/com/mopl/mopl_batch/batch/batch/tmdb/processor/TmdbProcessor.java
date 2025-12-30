package com.mopl.mopl_batch.batch.batch.tmdb.processor;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.mopl.mopl_batch.batch.Repository.ContentRepository;
import com.mopl.mopl_batch.batch.batch.tmdb.dto.ContentSaveDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class TmdbProcessor implements ItemProcessor<ContentSaveDto, ContentSaveDto> {

	private final ContentRepository contentRepository;

	@Override
	public ContentSaveDto process(ContentSaveDto item) {

		if (contentRepository.existsBySourceId(item.getSourceId())) {
			return null;
		}

		log.info("[TmdbProcessor.process] item.id: {}", item.getSourceId());
		log.info("[TmdbProcessor.process] item.type: {}", item.getType());

		return item;
	}
}
