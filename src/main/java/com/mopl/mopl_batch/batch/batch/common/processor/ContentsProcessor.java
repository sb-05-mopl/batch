package com.mopl.mopl_batch.batch.batch.common.processor;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.mopl.mopl_batch.batch.Repository.ContentRepository;
import com.mopl.mopl_batch.batch.batch.common.dto.ContentWithTags;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ContentsProcessor implements ItemProcessor<ContentWithTags, ContentWithTags> {

	private final ContentRepository contentRepository;

	@Override
	public ContentWithTags process(ContentWithTags item) {

		if (contentRepository.existsBySourceIdAndType(item.getContent().getSourceId(), item.getContent().getType())) {
			return null;
		}

		return item;
	}
}
