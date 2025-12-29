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
import com.mopl.mopl_batch.batch.batch.tmdb.processor.TmdbMovieProcessor;
import com.mopl.mopl_batch.batch.batch.tmdb.reader.TmdbMovieReader;
import com.mopl.mopl_batch.batch.batch.tmdb.writer.TmdbMovieWriter;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class TmdbMovieJob {

	private final JobRepository jobRepository;

	private final PlatformTransactionManager platformTransactionManager;

	private final TmdbMovieWriter tmdbMovieWriter;
	private final TmdbMovieProcessor tmdbMovieProcessor;
	private final TmdbMovieReader tmdbMovieReader;

	@Bean
	public Step tmdbMovieStep() {
		int chuck = 1000;

		return new StepBuilder("tmdbMovieStep", jobRepository)
			.<ContentSaveDto, ContentSaveDto>chunk(chuck, platformTransactionManager)
			.reader(tmdbMovieReader)
			.processor(tmdbMovieProcessor)
			.writer(tmdbMovieWriter)
			.build();
	}

	@Bean
	public Job tmdbJob() {
		return new JobBuilder("tmdbJob", jobRepository)
			.start(tmdbMovieStep())
			.build();
	}

}
