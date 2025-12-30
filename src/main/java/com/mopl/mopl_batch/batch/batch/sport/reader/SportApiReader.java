package com.mopl.mopl_batch.batch.batch.sport.reader;

import java.time.LocalDate;
import java.util.List;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.stereotype.Component;

import com.mopl.mopl_batch.batch.batch.common.ContentSaveDto;
import com.mopl.mopl_batch.batch.batch.sport.client.SportsApiClient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@StepScope
@RequiredArgsConstructor
public class SportApiReader implements ItemStreamReader<ContentSaveDto> {

	private final SportsApiClient sportsApiRestClient;

	private LocalDate startDate;
	private int currentDate;
	private int currentIndex;
	private List<ContentSaveDto> currentData;

	private static final int MAX_DATE = 100;
	private static final String START_DATE_KEY = "sports.start.date";
	private static final String CURRENT_DATE_KEY = "sports.current.date";
	private static final String CURRENT_INDEX_KEY = "sports.current.index";

	@Override
	public void open(ExecutionContext executionContext) throws ItemStreamException {
		if (executionContext.containsKey(START_DATE_KEY)) {
			startDate = LocalDate.parse(executionContext.getString(START_DATE_KEY));
			currentDate = executionContext.getInt(CURRENT_DATE_KEY);
			currentIndex = executionContext.getInt(CURRENT_INDEX_KEY);
		} else {
			startDate = LocalDate.now().plusDays(MAX_DATE / 2);
			currentDate = 1;
			currentIndex = 0;
		}
		currentData = null;
		log.info("[SportApiReader.open] startDate: {}, currentDate: {}, currentIndex: {}", startDate, currentDate,
			currentIndex);
	}

	@Override
	public ContentSaveDto read() {
		while (true) {

			if (currentDate > MAX_DATE) {
				return null;
			}

			LocalDate targetDate = startDate.minusDays(currentDate);

			if (currentData == null) {
				currentData = sportsApiRestClient.fetchContent(targetDate);

				if (currentData == null || currentData.isEmpty()) {
					currentDate++;
					continue;
				}
			}

			if (currentIndex >= currentData.size()) {
				log.info("[SportApiReader.read] currentData size is over. currentDate: [{}]", currentDate);
				currentDate++;
				currentIndex = 0;
				currentData = null;
				continue;
			}

			return currentData.get(currentIndex++);
		}
	}

	@Override
	public void update(ExecutionContext executionContext) throws ItemStreamException {
		executionContext.putString(START_DATE_KEY, startDate.toString());
		executionContext.putInt(CURRENT_DATE_KEY, currentDate);
		executionContext.putInt(CURRENT_INDEX_KEY, currentIndex);
	}

	@Override
	public void close() throws ItemStreamException {
		currentData = null;
		startDate = null;
		currentDate = 0;
		currentIndex = 0;
	}
}