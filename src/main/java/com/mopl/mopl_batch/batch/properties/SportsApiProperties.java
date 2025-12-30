package com.mopl.mopl_batch.batch.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "data.sport-api")
public class SportsApiProperties {
	private final String baseUrl;
}
