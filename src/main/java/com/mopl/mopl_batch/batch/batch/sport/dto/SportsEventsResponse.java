package com.mopl.mopl_batch.batch.batch.sport.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class SportsEventsResponse {

	@JsonProperty("events")
	private List<EventDto> events;

	@Data
	@Builder
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class EventDto {

		private String idEvent;
		private String idAPIfootball;

		private String strEvent;
		private String strEventAlternate;
		private String strFilename;

		private String strSport;

		private String idLeague;
		private String strLeague;
		private String strLeagueBadge;

		private String strSeason;
		private String strDescriptionEN;

		private String strHomeTeam;
		private String strAwayTeam;

		private String intHomeScore;
		private String intAwayScore;
		private String intRound;
		private String intScore;
		private String intScoreVotes;

		private Integer intSpectators;

		private String strOfficial;

		private String strTimestamp;
		private String dateEvent;
		private String dateEventLocal;
		private String strTime;
		private String strTimeLocal;

		private String strGroup;

		private String idHomeTeam;
		private String idAwayTeam;

		private String strHomeTeamBadge;
		private String strAwayTeamBadge;

		private String strResult;

		private String idVenue;
		private String strVenue;

		private String strCountry;
		private String strCity;

		private String strPoster;
		private String strSquare;
		private String strFanart;
		private String strThumb;
		private String strBanner;

		private String strMap;

		private String strTweet1;
		private String strVideo;

		private String strStatus;
		private String strPostponed;
		private String strLocked;
	}
}

