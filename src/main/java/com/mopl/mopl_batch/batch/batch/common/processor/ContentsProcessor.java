package com.mopl.mopl_batch.batch.batch.common.processor;

import com.mopl.mopl_batch.batch.batch.metric.BatchMetricsService;
import com.mopl.mopl_batch.batch.entity.Type;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
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
	private final BatchMetricsService batchMetricsService;

	@Override
	public ContentFetchDto process(ContentFetchDto item) {
		boolean isDuplicate = contentRepository.existsBySourceIdAndType(item.getSourceId(), item.getType());

		if (isDuplicate) {
			batchMetricsService.incrementDuplicateCount(item.getType(), 1);
			return null;
		}

		batchMetricsService.incrementNewCount(item.getType(), 1);
		return item;
	}


}
