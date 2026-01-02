package com.mopl.mopl_batch.batch.batch.common.processor;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.mopl.mopl_batch.batch.Repository.ContentRepository;
import com.mopl.mopl_batch.batch.batch.common.dto.ContentFetchDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ContentsProcessor implements ItemProcessor<ContentFetchDto, ContentFetchDto> {

	private final ContentRepository contentRepository;

	@Override
	public ContentFetchDto process(ContentFetchDto item) {

		if (contentRepository.existsBySourceIdAndType(item.getSourceId(), item.getType())) {
			return null;
		}

		return item;
	}
}
