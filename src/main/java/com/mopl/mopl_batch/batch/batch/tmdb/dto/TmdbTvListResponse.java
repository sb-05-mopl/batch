package com.mopl.mopl_batch.batch.batch.tmdb.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class TmdbTvListResponse {

	private int page;

	@JsonProperty("results")
	private List<TmdbTvDto> results;

	@JsonProperty("total_pages")
	private int totalPages;

	@JsonProperty("total_results")
	private int totalResults;

	@Data
	@Builder
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class TmdbTvDto {

		private boolean adult;

		@JsonProperty("backdrop_path")
		private String backdropPath;

		@JsonProperty("genre_ids")
		private List<Integer> genreIds;

		private long id;

		@JsonProperty("origin_country")
		private List<String> originCountry;

		@JsonProperty("original_language")
		private String originalLanguage;

		@JsonProperty("original_name")
		private String originalName;

		private String overview;

		private double popularity;

		@JsonProperty("poster_path")
		private String posterPath;

		@JsonProperty("first_air_date")
		private String firstAirDate;

		private String name;

		@JsonProperty("vote_average")
		private double voteAverage;

		@JsonProperty("vote_count")
		private int voteCount;
	}
}
