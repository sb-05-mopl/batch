package com.mopl.mopl_batch.batch.schedule;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class CollectContentScheduler {

	private final JobLauncher jobLauncher;
	private final Job tmdbJob;

	@PostConstruct
	// @Scheduled(cron = "0 0 1 * * *")
	public void runCollectContentJob() {

		try {
			JobParameters jobParameters = new JobParametersBuilder().toJobParameters();
			jobLauncher.run(tmdbJob, jobParameters);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
