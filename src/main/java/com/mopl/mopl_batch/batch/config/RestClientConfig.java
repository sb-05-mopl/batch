package com.mopl.mopl_batch.batch.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

import com.mopl.mopl_batch.batch.properties.TmdbProperties;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class RestClientConfig {

	private final TmdbProperties tmdbProperties;

	@Bean
	public RestClient tmdbRestClient() {
		return RestClient.builder()
			.baseUrl(tmdbProperties.getBaseUrl())
			.defaultHeader("accept", "application/json")
			.defaultHeader("Authorization", "Bearer " + tmdbProperties.getReadAccessToken())
			.requestInterceptor((request, body, execution) -> {
				log.debug("===== TMDB REQUEST =====");
				log.debug("METHOD  : {}", request.getMethod());
				log.debug("URI     : {}", request.getURI());
				log.debug("HEADERS : {}", request.getHeaders());
				log.debug("========================");
				return execution.execute(request, body);
			})
			.build();
	}
}
