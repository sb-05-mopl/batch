package com.mopl.mopl_batch.batch.batch.common.processor;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.mopl.mopl_batch.batch.Repository.ContentRepository;
import com.mopl.mopl_batch.batch.batch.common.ContentSaveDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ContentsProcessor implements ItemProcessor<ContentSaveDto, ContentSaveDto> {

	private final ContentRepository contentRepository;

	@Override
	public ContentSaveDto process(ContentSaveDto item) {

		if (contentRepository.existsBySourceIdAndType(item.getSourceId(), item.getType())) {
			return null;
		}

		return item;
	}
}
