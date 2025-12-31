package com.mopl.mopl_batch.batch.batch.tmdb.client;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.mopl.mopl_batch.batch.batch.common.dto.ContentSaveDto;
import com.mopl.mopl_batch.batch.batch.tmdb.dto.TmdbGenreListResponse;
import com.mopl.mopl_batch.batch.batch.tmdb.dto.TmdbMovieListResponse;
import com.mopl.mopl_batch.batch.batch.tmdb.dto.TmdbTvListResponse;
import com.mopl.mopl_batch.batch.entity.Type;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class TmdbClient {

	private final RestClient tmdbRestClient;

	public TmdbClient(
		@Qualifier("tmdbRestClient") RestClient sportsApiRestClient) {
		this.tmdbRestClient = sportsApiRestClient;
	}

	public List<ContentSaveDto> fetchContent(Type type, int page) {
		return switch (type) {
			case MOVIE -> fetchMovieContent(type, page);
			case TV_SERIES -> fetchTvContent(type, page);
			default -> Collections.emptyList();
		};
	}

	public Map<Long, String> getGenres(Type type) {
		return switch (type) {
			case MOVIE -> getGenresByType("movie");
			case TV_SERIES -> getGenresByType("tv");
			default -> Map.of();
		};
	}

	private Map<Long, String> getGenresByType(String typeStr) {
		TmdbGenreListResponse response = tmdbRestClient.get()
			.uri(uriBuilder -> uriBuilder
				.pathSegment("3", "genre", typeStr, "list")
				.queryParam("language", "ko-KR")
				.build()
			)
			.retrieve()
			.body(TmdbGenreListResponse.class);

		if (response == null || response.getGenres() == null) {
			return Map.of();
		}

		Map<Long, String> genres = new HashMap<>();
		for (TmdbGenreListResponse.GenreDto genre : response.getGenres()) {
			genres.put(genre.getId(), genre.getName());
		}

		return genres;
	}

	private List<ContentSaveDto> fetchMovieContent(Type type, int page) {
		TmdbMovieListResponse response = tmdbRestClient.get()
			.uri(uriBuilder -> uriBuilder
				.path("/3/discover/movie")
				.queryParam("3", "discover", "movie")
				.queryParam("sort_by", "popularity.desc")
				.queryParam("language", "ko-KR")
				.queryParam("page", page)
				.build()
			)
			.retrieve()
			.body(TmdbMovieListResponse.class);

		if (isResponseInvalid(response, response != null ? response.getResults() : null)) {
			return Collections.emptyList();
		}

		return response.getResults().stream()
			.map(movie -> ContentSaveDto.builder()
				.title(movie.getTitle())
				.type(type)
				.description(movie.getOverview())
				.thumbnailUrl(movie.getPosterPath())
				.sourceId(movie.getId())
				.build())
			.toList();
	}

	private List<ContentSaveDto> fetchTvContent(Type type, int page) {
		TmdbTvListResponse response = tmdbRestClient.get()
			.uri(uriBuilder -> uriBuilder
				.pathSegment("3", "discover", "tv")
				.queryParam("sort_by", "popularity.desc")
				.queryParam("language", "ko-KR")
				.queryParam("page", page)
				.build()
			)
			.retrieve()
			.body(TmdbTvListResponse.class);

		if (isResponseInvalid(response, response != null ? response.getResults() : null)) {
			return Collections.emptyList();
		}

		return response.getResults().stream()
			.map(tv -> ContentSaveDto.builder()
				.title(tv.getName())
				.type(type)
				.description(tv.getOverview())
				.thumbnailUrl(tv.getPosterPath())
				.sourceId(tv.getId())
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