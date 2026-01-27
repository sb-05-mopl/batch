package com.mopl.mopl_batch.batch.batch.job;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class JobConfiguration {

	private final JobRepository jobRepository;
	private final Step tmdbMovieStep;
	private final Step tmdbTvStep;
	private final Step sportApiStep;
	@Bean
	public Job fetchTmdbContentsJob() {
		return new JobBuilder("fetchTmdbContentsJob", jobRepository)
			.start(tmdbMovieStep)
			.next(tmdbTvStep)
			.build();
	}

	@Bean
	public Job fetchSportContentsJob() {
		return new JobBuilder("fetchSportContentsJob", jobRepository)
			.start(sportApiStep)
			.build();
	}
}