package com.mopl.mopl_batch.batch.batch.sport.listener;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.ExecutionContext;

import com.mopl.mopl_batch.batch.entity.Type;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class SportStepListener implements StepExecutionListener {

	private final Type contentType;

	@Override
	public void beforeStep(StepExecution stepExecution) {
		ExecutionContext context = stepExecution.getExecutionContext();
		log.info("[SportStepListener][Step Start]");
	}

	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {
		log.info("[SportStepListener][Step 완료 - Type: {}, Read: {}, Write: {}]",
			contentType,
			stepExecution.getReadCount(),
			stepExecution.getWriteCount()
		);
		return stepExecution.getExitStatus();
	}
}
