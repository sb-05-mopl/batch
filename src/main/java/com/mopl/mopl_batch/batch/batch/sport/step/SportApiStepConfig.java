package com.mopl.mopl_batch.batch.batch.sport.step;

import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import com.mopl.mopl_batch.batch.batch.common.ContentSaveDto;
import com.mopl.mopl_batch.batch.batch.common.processor.ContentsProcessor;
import com.mopl.mopl_batch.batch.batch.common.writer.ContentsWriter;
import com.mopl.mopl_batch.batch.batch.sport.listener.SportStepListener;
import com.mopl.mopl_batch.batch.batch.sport.reader.SportApiReader;
import com.mopl.mopl_batch.batch.entity.Type;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class SportApiStepConfig {

	private final JobRepository jobRepository;
	private final PlatformTransactionManager transactionManager;
	private final ContentsWriter contentsWriter;
	private final ContentsProcessor contentsProcessor;
	private final SportApiReader sportApiReader;

	private static final int CHUNK_SIZE = 100;

	@Bean
	public Step sportApiStep() {
		return new StepBuilder("sportApiStep", jobRepository)
			.<ContentSaveDto, ContentSaveDto>chunk(CHUNK_SIZE, transactionManager)
			.reader(sportApiReader)
			.processor(contentsProcessor)
			.writer(contentsWriter)
			.listener(new SportStepListener(Type.SPORTS))
			.build();
	}
}