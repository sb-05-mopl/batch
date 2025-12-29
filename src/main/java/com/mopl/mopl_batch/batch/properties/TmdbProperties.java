package com.mopl.mopl_batch.batch.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "data.tmdb")
public class TmdbProperties {
	private final String key;
	private final String readAccessToken;
	private final String baseUrl;
}
