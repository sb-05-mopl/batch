package com.mopl.mopl_batch.batch.batch.sport.listener;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class SportStepListener implements StepExecutionListener {

	@Override
	public void beforeStep(StepExecution stepExecution) {
		log.info("[SportStepListener][Step Start]");
	}

	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {
		log.info("[SportStepListener][Step 완료 - Read: {}, Write: {}]",
			stepExecution.getReadCount(),
			stepExecution.getWriteCount()
		);
		return stepExecution.getExitStatus();
	}
}
