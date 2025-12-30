package com.mopl.mopl_batch.batch.batch.sport.client;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import com.mopl.mopl_batch.batch.batch.common.ContentSaveDto;
import com.mopl.mopl_batch.batch.batch.common.util.DateFormatUtil;
import com.mopl.mopl_batch.batch.batch.sport.dto.SportsEventsResponse;
import com.mopl.mopl_batch.batch.batch.sport.dto.SportsEventsResponse.EventDto;
import com.mopl.mopl_batch.batch.entity.Type;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class SportsApiClient {

	private final RestClient sportsApiRestClient;

	public SportsApiClient(@Qualifier("sportApiRestClient") RestClient sportsApiRestClient) {
		this.sportsApiRestClient = sportsApiRestClient;
	}

	public List<ContentSaveDto> fetchContent(LocalDate date) {
		String dateString = DateFormatUtil.toString(date);

		try {
			return doFetch(dateString);
		} catch (HttpClientErrorException.TooManyRequests e) {
			log.warn("Sports API 429 Too Many Requests 발생. 60초 대기 후 재시도");

			try {
				Thread.sleep(60_000);
			} catch (InterruptedException ie) {
				Thread.currentThread().interrupt();
				log.error("Sleep interrupted", ie);
				return Collections.emptyList();
			}

			try {
				return doFetch(dateString);
			} catch (Exception retryException) {
				log.error("Sports API 재시도 실패", retryException);
				return Collections.emptyList();
			}
		} catch (Exception e) {
			log.error("Sports API 호출 실패", e);
			return Collections.emptyList();
		}
	}

	private List<ContentSaveDto> doFetch(String dateString) {
		SportsEventsResponse response = sportsApiRestClient.get()
			.uri(uriBuilder -> uriBuilder
				.queryParam("s", "Soccer")
				.queryParam("d", dateString)
				.build())
			.retrieve()
			.body(SportsEventsResponse.class);

		if (response == null || response.getEvents() == null || response.getEvents().isEmpty()) {
			log.warn("Sports API response is null or empty");
			return Collections.emptyList();
		}

		return response.getEvents().stream()
			.map(this::toContentSaveDto)
			.toList();
	}

	private ContentSaveDto toContentSaveDto(EventDto event) {
		return ContentSaveDto.builder()
			.title(event.getStrEvent())
			.type(Type.SPORTS)
			.description(event.getStrFilename())
			.thumbnailUrl(getThumbnailUrl(event))
			.sourceId(Long.parseLong(event.getIdEvent()))
			.build();
	}

	private String getThumbnailUrl(EventDto event) {
		if (StringUtils.hasText(event.getStrThumb())) {
			return event.getStrThumb();
		}
		return StringUtils.hasText(event.getStrCity()) ?
			event.getStrBanner() : event.getStrLeagueBadge();
	}
}