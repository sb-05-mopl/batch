package com.mopl.mopl_batch.batch.batch.tmdb.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;

import com.mopl.mopl_batch.batch.batch.common.dto.ContentFetchDto;
import com.mopl.mopl_batch.batch.batch.tmdb.dto.TmdbGenreListResponse;
import com.mopl.mopl_batch.batch.batch.tmdb.dto.TmdbMovieListResponse;
import com.mopl.mopl_batch.batch.batch.tmdb.dto.TmdbTvListResponse;
import com.mopl.mopl_batch.batch.entity.Type;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class TmdbClient {

	private final RestClient tmdbRestClient;

	private final Map<Integer, String> genres;
	private final static long TOO_MANY_REQUESTS_EXCEPTION = 60_000L;
	private final static long HTTP_SERVER_ERROR_EXCEPTION_SLEEP = 5_000L;

	public TmdbClient(
		@Qualifier("tmdbRestClient") RestClient sportsApiRestClient) {
		this.tmdbRestClient = sportsApiRestClient;
		this.genres = setGenres();
	}

	public List<ContentFetchDto> fetchContent(Type type, int page) {
		int maxAttempts = 3;
		int attempt = 0;

		while (true) {
			try {
				return switch (type) {
					case MOVIE -> fetchMovieContent(type, page);
					case TV_SERIES -> fetchTvContent(type, page);
					default -> Collections.emptyList();
				};

			} catch (HttpClientErrorException.TooManyRequests e) {
				attempt++;
				if (attempt >= maxAttempts)
					throw e;
				sleep(TOO_MANY_REQUESTS_EXCEPTION);
			} catch (HttpServerErrorException e) {
				attempt++;
				if (attempt >= maxAttempts)
					throw e;
				sleep(HTTP_SERVER_ERROR_EXCEPTION_SLEEP);
			}
		}
	}

	private Map<Integer, String> getGenresByType(String typeStr) {
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

		Map<Integer, String> genres = new HashMap<>();
		for (TmdbGenreListResponse.GenreDto genre : response.getGenres()) {
			genres.put(genre.getId(), genre.getName());
		}

		return genres;
	}

	private List<ContentFetchDto> fetchMovieContent(Type type, int page) {
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

		List<ContentFetchDto> result = new ArrayList<>();
		List<TmdbMovieListResponse.TmdbMovieDto> results = response.getResults();
		for (TmdbMovieListResponse.TmdbMovieDto dto : results) {

			List<Integer> genreIds = dto.getGenreIds();
			List<String> tags = Stream.concat(
				genreIds.stream().map(this::getGenreById),
				Stream.of(type.getTypeTag())
			).toList();

			result.add(ContentFetchDto.builder()
				.title(dto.getTitle())
				.type(type)
				.description(dto.getOverview())
				.thumbnailUrl(dto.getPosterPath())
				.sourceId(dto.getId())
				.tags(new HashSet<>(tags))
				.build());
		}
		return result;

	}

	private List<ContentFetchDto> fetchTvContent(Type type, int page) {
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

		List<ContentFetchDto> result = new ArrayList<>();
		List<TmdbTvListResponse.TmdbTvDto> results = response.getResults();

		for (TmdbTvListResponse.TmdbTvDto dto : results) {
			List<Integer> genreIds = dto.getGenreIds();
			List<String> tags = Stream.concat(
				genreIds.stream().map(this::getGenreById),
				Stream.of(type.getTypeTag())
			).toList();

			result.add(ContentFetchDto.builder()
				.title(dto.getName())
				.type(type)
				.description(dto.getOverview())
				.thumbnailUrl(dto.getPosterPath())
				.sourceId(dto.getId())
				.tags(new HashSet<>(tags))
				.build());
		}
		return result;
	}

	private boolean isResponseInvalid(Object response, Object results) {
		if (response == null || results == null) {
			log.warn("TMDB API response is null or empty");
			return true;
		}
		return false;
	}

	private String getGenreById(int genreId) {
		return genres.getOrDefault(genreId, "");
	}

	private Map<Integer, String> setGenres() {
		Map<Integer, String> allGenres = new HashMap<>();
		allGenres.putAll(getGenresByType("movie"));
		allGenres.putAll(getGenresByType("tv"));
		return allGenres;
	}

	private void sleep(long ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException ie) {
			Thread.currentThread().interrupt();
			throw new IllegalStateException("Interrupted while retrying", ie);
		}
	}

}