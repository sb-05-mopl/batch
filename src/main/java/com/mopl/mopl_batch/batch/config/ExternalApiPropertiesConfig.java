package com.mopl.mopl_batch.batch.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import com.mopl.mopl_batch.batch.properties.TmdbProperties;

@Configuration
@EnableConfigurationProperties({TmdbProperties.class})
public class ExternalApiPropertiesConfig {
}
