package com.mopl.mopl_batch.batch.schedule;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class CollectContentScheduler {

	private final JobLauncher jobLauncher;
	private final Job fetchSportContentsJob;
	private final Job fetchTmdbContentsJob;

	// @PostConstruct // 실행하고 딱 한번 실행
	@Scheduled(cron = "${spring.batch.schedule.tmdb}")
	public void setFetchTmdbContentsJob() {
		try {

			JobParameters jobParameters = new JobParametersBuilder()
				.addLong("runTime", System.currentTimeMillis()) // 개발용
				// .addString("runDay", LocalDate.now().toString()) // 배포용
				.toJobParameters();
			jobLauncher.run(fetchTmdbContentsJob, jobParameters);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Scheduled(cron = "${spring.batch.schedule.sport-api}")
	public void setFetchSportContentsJob() {
		try {
			JobParameters jobParameters = new JobParametersBuilder()
				.addLong("runTime", System.currentTimeMillis()) // 개발용
				// .addString("runDay", LocalDate.now().toString()) // 배포용
				.toJobParameters();
			jobLauncher.run(fetchSportContentsJob, jobParameters);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
