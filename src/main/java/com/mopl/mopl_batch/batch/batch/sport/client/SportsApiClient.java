package com.mopl.mopl_batch.batch.batch.sport.client;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import com.mopl.mopl_batch.batch.batch.common.dto.ContentFetchDto;
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

	public List<ContentFetchDto> fetchContent(LocalDate date) {
		String dateString = DateFormatUtil.toString(date);

		try {
			return doFetch(dateString);
		} catch (HttpClientErrorException.TooManyRequests e) {
			log.warn("[SportsApiClient.fetchContent] Sports API 429 Too Many Requests wait 60s");

			try {
				Thread.sleep(60_000);
			} catch (InterruptedException ie) {
				Thread.currentThread().interrupt();
				log.error("[SportsApiClient.fetchContent] Sleep interrupted", ie);
				return Collections.emptyList();
			}

			try {
				return doFetch(dateString);
			} catch (Exception retryException) {
				log.error("[SportsApiClient.fetchContent] Sports API 재시도 실패", retryException);
				return Collections.emptyList();
			}
		} catch (Exception e) {
			log.error("[SportsApiClient.fetchContent] Sports API 호출 실패", e);
			return Collections.emptyList();
		}
	}

	private List<ContentFetchDto> doFetch(String dateString) {
		SportsEventsResponse response = sportsApiRestClient.get()
			.uri(uriBuilder -> uriBuilder
				.queryParam("s", "Soccer")
				.queryParam("d", dateString)
				.build())
			.retrieve()
			.body(SportsEventsResponse.class);

		if (response == null || response.getEvents() == null || response.getEvents().isEmpty()) {
			log.warn("[SportsApiClient.doFetch] Sports API response is null or empty");
			return Collections.emptyList();
		}

		return response.getEvents().stream()
			.map(this::toContentSaveDto)
			.toList();
	}

	private ContentFetchDto toContentSaveDto(EventDto event) {

		Set<String> tags = new HashSet<>();
		tags.add(Type.SPORTS.getTypeTag());

		addIfNotBlank(tags, event.getStrSport());
		addIfNotBlank(tags, event.getStrLeague());
		addIfNotBlank(tags, event.getStrVenue());

		return ContentFetchDto.builder()
			.title(event.getStrEvent())
			.type(Type.SPORTS)
			.description(event.getStrFilename())
			.thumbnailUrl(getThumbnailUrl(event))
			.sourceId(Long.parseLong(event.getIdEvent()))
			.tags(tags)
			.build();
	}

	private void addIfNotBlank(Set<String> set, String value) {
		if (value != null && !value.isBlank()) {
			set.add(value);
		}
	}

	private String getThumbnailUrl(EventDto event) {
		if (StringUtils.hasText(event.getStrThumb())) {
			return event.getStrThumb();
		}
		return StringUtils.hasText(event.getStrCity()) ?
			event.getStrBanner() : event.getStrLeagueBadge();
	}
}