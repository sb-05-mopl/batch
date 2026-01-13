package com.mopl.mopl_batch.batch.batch.tmdb.step;

import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.backoff.BackOffPolicy;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.client.ResourceAccessException;

import com.mopl.mopl_batch.batch.batch.common.dto.ContentFetchDto;
import com.mopl.mopl_batch.batch.batch.common.processor.ContentsProcessor;
import com.mopl.mopl_batch.batch.batch.common.writer.ContentsWriter;
import com.mopl.mopl_batch.batch.batch.tmdb.listener.TmdbStepListener;
import com.mopl.mopl_batch.batch.batch.tmdb.reader.TmdbReader;
import com.mopl.mopl_batch.batch.entity.Type;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class TmdbStepConfig {

	private final JobRepository jobRepository;
	private final PlatformTransactionManager transactionManager;
	private final ContentsWriter contentsWriter;
	private final ContentsProcessor contentsProcessor;
	private final TmdbReader tmdbReader;

	private static final int CHUNK_SIZE = 1000;

	@Bean
	public Step tmdbMovieStep() {
		return createTmdbStep("tmdbMovieStep", Type.MOVIE);
	}

	@Bean
	public Step tmdbTvStep() {
		return createTmdbStep("tmdbTvStep", Type.TV_SERIES);
	}

	private Step createTmdbStep(String stepName, Type contentType) {
		return new StepBuilder(stepName, jobRepository)
			.<ContentFetchDto, ContentFetchDto>chunk(CHUNK_SIZE, transactionManager)
			.reader(tmdbReader)
			.processor(contentsProcessor)
			.writer(contentsWriter)
			.listener(new TmdbStepListener(contentType))
			.faultTolerant()
			.retry(ResourceAccessException.class)
			.retryLimit(3)
			.backOffPolicy(exponentialBackOffPolicy())
			.build();
	}
	@Bean
	public BackOffPolicy exponentialBackOffPolicy() {
		ExponentialBackOffPolicy policy = new ExponentialBackOffPolicy();
		policy.setInitialInterval(2000);
		policy.setMultiplier(2.0);
		policy.setMaxInterval(10000);
		return policy;
	}
}