package com.mopl.mopl_batch.batch.batch.tmdb.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class TmdbMovieListResponse {

	private int page;

	@JsonProperty("results")
	private List<TmdbMovieDto> results;

	@JsonProperty("total_pages")
	private int totalPages;

	@JsonProperty("total_results")
	private int totalResults;

	@Data
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class TmdbMovieDto {

		private boolean adult;

		@JsonProperty("backdrop_path")
		private String backdropPath;

		@JsonProperty("genre_ids")
		private List<Integer> genreIds;

		private long id;

		@JsonProperty("original_language")
		private String originalLanguage;

		@JsonProperty("original_title")
		private String originalTitle;

		private String overview;

		private double popularity;

		@JsonProperty("poster_path")
		private String posterPath;

		@JsonProperty("release_date")
		private String releaseDate;

		private String title;

		private boolean video;

		@JsonProperty("vote_average")
		private double voteAverage;

		@JsonProperty("vote_count")
		private int voteCount;

		public TmdbMovieDto() {
		}

		public boolean isAdult() {
			return adult;
		}

		public void setAdult(boolean adult) {
			this.adult = adult;
		}

		public String getBackdropPath() {
			return backdropPath;
		}

		public void setBackdropPath(String backdropPath) {
			this.backdropPath = backdropPath;
		}

		public List<Integer> getGenreIds() {
			return genreIds;
		}

		public void setGenreIds(List<Integer> genreIds) {
			this.genreIds = genreIds;
		}

		public long getId() {
			return id;
		}

		public void setId(long id) {
			this.id = id;
		}

		public String getOriginalLanguage() {
			return originalLanguage;
		}

		public void setOriginalLanguage(String originalLanguage) {
			this.originalLanguage = originalLanguage;
		}

		public String getOriginalTitle() {
			return originalTitle;
		}

		public void setOriginalTitle(String originalTitle) {
			this.originalTitle = originalTitle;
		}

		public String getOverview() {
			return overview;
		}

		public void setOverview(String overview) {
			this.overview = overview;
		}

		public double getPopularity() {
			return popularity;
		}

		public void setPopularity(double popularity) {
			this.popularity = popularity;
		}

		public String getPosterPath() {
			return posterPath;
		}

		public void setPosterPath(String posterPath) {
			this.posterPath = posterPath;
		}

		public String getReleaseDate() {
			return releaseDate;
		}

		public void setReleaseDate(String releaseDate) {
			this.releaseDate = releaseDate;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public boolean isVideo() {
			return video;
		}

		public void setVideo(boolean video) {
			this.video = video;
		}

		public double getVoteAverage() {
			return voteAverage;
		}

		public void setVoteAverage(double voteAverage) {
			this.voteAverage = voteAverage;
		}

		public int getVoteCount() {
			return voteCount;
		}

		public void setVoteCount(int voteCount) {
			this.voteCount = voteCount;
		}
	}
}
