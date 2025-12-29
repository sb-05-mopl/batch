package com.mopl.mopl_batch.batch.batch.tmdb.client;

import lombok.Getter;

@Getter
public enum Language {
	EN("en-US"), KO("ko-KR");

	private final String code;

	Language(String code) {
		this.code = code;
	}
}
