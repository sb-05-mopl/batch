package com.mopl.mopl_batch.batch.batch.tmdb.listener;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.ExecutionContext;

import com.mopl.mopl_batch.batch.batch.tmdb.reader.TmdbReader;
import com.mopl.mopl_batch.batch.entity.Type;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class TmdbStepListener implements StepExecutionListener {

	private final Type contentType;

	@Override
	public void beforeStep(StepExecution stepExecution) {
		ExecutionContext context = stepExecution.getExecutionContext();
		context.put(TmdbReader.CURRENT_TYPE_KEY, contentType);
		log.info("[TmdbStepListener][Step Start - Type: {}]", contentType);
	}

	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {
		log.info("[TmdbStepListener][Step 완료 - Type: {}, Read: {}, Write: {}]",
			contentType,
			stepExecution.getReadCount(),
			stepExecution.getWriteCount()
		);
		return stepExecution.getExitStatus();
	}
}
