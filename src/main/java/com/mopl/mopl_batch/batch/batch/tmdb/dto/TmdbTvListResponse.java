package com.mopl.mopl_batch.batch.batch.tmdb.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TmdbTvListResponse(
	int page,
	@JsonProperty("results") List<TmdbTvDto> results,
	@JsonProperty("total_pages") int totalPages,
	@JsonProperty("total_results") int totalResults
) {
	@JsonIgnoreProperties(ignoreUnknown = true)
	public record TmdbTvDto(

		boolean adult,

		@JsonProperty("backdrop_path")
		String backdropPath,

		@JsonProperty("genre_ids")
		List<Integer> genreIds,

		long id,

		@JsonProperty("origin_country")
		List<String> originCountry,

		@JsonProperty("original_language")
		String originalLanguage,

		@JsonProperty("original_name")
		String originalName,

		String overview,

		double popularity,

		@JsonProperty("poster_path")
		String posterPath,

		@JsonProperty("first_air_date")
		String firstAirDate,

		String name,

		@JsonProperty("vote_average")
		double voteAverage,

		@JsonProperty("vote_count")
		int voteCount
	) {
	}
}
