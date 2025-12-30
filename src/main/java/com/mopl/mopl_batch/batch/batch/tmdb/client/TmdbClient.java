package com.mopl.mopl_batch.batch.batch.tmdb.client;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.mopl.mopl_batch.batch.batch.tmdb.dto.ContentSaveDto;
import com.mopl.mopl_batch.batch.batch.tmdb.dto.TmdbMovieListResponse;
import com.mopl.mopl_batch.batch.batch.tmdb.dto.TmdbTvListResponse;
import com.mopl.mopl_batch.batch.entity.Type;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class TmdbClient {

	private final RestClient tmdbRestClient;

	public List<ContentSaveDto> fetchContent(Type type, int page) {
		return switch (type) {
			case MOVIE -> fetchMovieContent(type, page);
			case TV_SERIES -> fetchTvContent(type, page);
			default -> Collections.emptyList();
		};
	}

	private List<ContentSaveDto> fetchMovieContent(Type type, int page) {
		TmdbMovieListResponse response = tmdbRestClient.get()
			.uri(uriBuilder -> uriBuilder
				.path("/movie")
				.queryParam("sort_by", "popularity.desc")
				.queryParam("language", "ko-KR")
				.queryParam("page", page)
				.build()
			)
			.retrieve()
			.body(TmdbMovieListResponse.class);

		if (isResponseInvalid(response, response != null ? response.results() : null)) {
			return Collections.emptyList();
		}

		return response.results().stream()
			.map(movie -> ContentSaveDto.builder()
				.title(movie.title())
				.type(type)
				.description(movie.overview())
				.thumbnailUrl(movie.posterPath())
				.sourceId(movie.id())
				.build())
			.toList();
	}

	private List<ContentSaveDto> fetchTvContent(Type type, int page) {
		TmdbTvListResponse response = tmdbRestClient.get()
			.uri(uriBuilder -> uriBuilder
				.path("/tv")
				.queryParam("sort_by", "popularity.desc")
				.queryParam("language", "ko-KR")
				.queryParam("page", page)
				.build()
			)
			.retrieve()
			.body(TmdbTvListResponse.class);

		if (isResponseInvalid(response, response != null ? response.results() : null)) {
			return Collections.emptyList();
		}

		return response.results().stream()
			.map(tv -> ContentSaveDto.builder()
				.title(tv.name())
				.type(type)
				.description(tv.overview())
				.thumbnailUrl(tv.posterPath())
				.sourceId(tv.id())
				.build())
			.toList();
	}

	private boolean isResponseInvalid(Object response, Object results) {
		if (response == null || results == null) {
			log.warn("TMDB API response is null or empty");
			return true;
		}
		return false;
	}
}