package com.mopl.mopl_batch.batch.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpRequest;
import org.springframework.web.client.RestClient;

import com.mopl.mopl_batch.batch.properties.SportsApiProperties;
import com.mopl.mopl_batch.batch.properties.TmdbProperties;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class RestClientConfig {

	private final TmdbProperties tmdbProperties;
	private final SportsApiProperties sportsApiProperties;

	@Bean("tmdbRestClient")
	public RestClient tmdbRestClient() {
		return RestClient.builder()
			.baseUrl(tmdbProperties.getBaseUrl())
			.defaultHeader("accept", "application/json")
			.defaultHeader("Authorization", "Bearer " + tmdbProperties.getReadAccessToken())
			.requestInterceptor((request, body, execution) -> {
				this.logRequest(request, "TMDB");
				return execution.execute(request, body);
			})
			.build();
	}

	@Bean("sportApiRestClient")
	public RestClient sportApiRestClient() {
		return RestClient.builder()
			.baseUrl(sportsApiProperties.getBaseUrl())
			.requestInterceptor(((request, body, execution) -> {
				this.logRequest(request, "SPORT_API");
				return execution.execute(request, body);
			})).build();
	}

	private void logRequest(HttpRequest request, String apiName) {
		log.debug("===== {}_REQUEST =====", apiName);
		log.debug("METHOD  : {}", request.getMethod());
		log.debug("URI     : {}", request.getURI());
		log.debug("HEADERS : {}", request.getHeaders());
		log.debug("========================");

	}
}
