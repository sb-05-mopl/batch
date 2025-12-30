package com.mopl.mopl_batch.batch.batch.tmdb.job;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import com.mopl.mopl_batch.batch.batch.tmdb.dto.ContentSaveDto;
import com.mopl.mopl_batch.batch.batch.tmdb.listener.TmdbStepListener;
import com.mopl.mopl_batch.batch.batch.tmdb.processor.TmdbProcessor;
import com.mopl.mopl_batch.batch.batch.tmdb.reader.TmdbReader;
import com.mopl.mopl_batch.batch.batch.tmdb.writer.TmdbWriter;
import com.mopl.mopl_batch.batch.entity.Type;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class TmdbJobConfiguration {

	private final JobRepository jobRepository;
	private final PlatformTransactionManager platformTransactionManager;

	private final TmdbWriter tmdbWriter;
	private final TmdbProcessor tmdbProcessor;
	private final TmdbReader tmdbReader;

	private static final int CHUNK_SIZE = 1000;

	@Bean
	public Job tmdbJob() {
		return new JobBuilder("tmdbJob", jobRepository)
			.start(tmdbMovieStep())
			.next(tmdbTvStep())
			.build();
	}

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
			.<ContentSaveDto, ContentSaveDto>chunk(CHUNK_SIZE, platformTransactionManager)
			.reader(tmdbReader)
			.processor(tmdbProcessor)
			.writer(tmdbWriter)
			.listener(new TmdbStepListener(contentType))
			.build();
	}

}