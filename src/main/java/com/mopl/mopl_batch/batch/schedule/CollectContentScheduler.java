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
	private final Job fetchSportContentsJob;
	private final Job fetchTmdbContentsJob;

	// @Scheduled(cron = "0 0 1 * * *") // 새벽 1시
	// @Scheduled(cron = "0 */3 * * * *") // 3분 마다
	@PostConstruct
	public void setFetchTmdbContentsJob() {
		try {
			JobParameters jobParameters = new JobParametersBuilder()
				.addLong("runTime", System.currentTimeMillis())
				.toJobParameters();
			jobLauncher.run(fetchTmdbContentsJob, jobParameters);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// @Scheduled(cron = "0 0 1 * * *")
	// @Scheduled(cron = "0 */3 * * * *")
	public void setFetchSportContentsJob() {
		try {
			JobParameters jobParameters = new JobParametersBuilder()
				.addLong("runTime", System.currentTimeMillis())
				.toJobParameters();
			jobLauncher.run(fetchSportContentsJob, jobParameters);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
