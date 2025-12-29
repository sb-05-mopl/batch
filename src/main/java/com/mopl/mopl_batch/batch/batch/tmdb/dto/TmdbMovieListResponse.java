package com.mopl.mopl_batch.batch.batch.tmdb.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TmdbMovieListResponse(

	int page,

	@JsonProperty("results")
	List<TmdbMovieDto> results,

	@JsonProperty("total_pages")
	int totalPages,

	@JsonProperty("total_results")
	int totalResults
) {

	@JsonIgnoreProperties(ignoreUnknown = true)
	public record TmdbMovieDto(
		boolean adult,

		@JsonProperty("backdrop_path")
		String backdropPath,
		@JsonProperty("genre_ids")
		List<Integer> genreIds,

		long id,

		@JsonProperty("original_language")
		String originalLanguage,

		@JsonProperty("original_title")
		String originalTitle,

		String overview,

		double popularity,

		@JsonProperty("poster_path")
		String posterPath,

		@JsonProperty("release_date")
		String releaseDate,

		String title,

		boolean video,

		@JsonProperty("vote_average")
		double voteAverage,

		@JsonProperty("vote_count")
		int voteCount
	) {
	}
}

