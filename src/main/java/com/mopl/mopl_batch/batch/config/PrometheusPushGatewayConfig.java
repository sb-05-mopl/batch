package com.mopl.mopl_batch.batch.config;

import io.micrometer.prometheusmetrics.PrometheusMeterRegistry;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class PrometheusPushGatewayConfig {

    private final PrometheusMeterRegistry prometheusMeterRegistry;

    @Value("${management.prometheus.metrics.export.pushgateway.base-url:http://localhost:9091}")
    private String pushgatewayUrl;

    @Value("${management.prometheus.metrics.export.pushgateway.job:batch-job}")
    private String jobName;

    @PostConstruct
    public void init() {
        log.info("=== PushGateway Configuration ===");
        log.info("PUSH_GATE_WAY_URL env: {}", System.getenv("PUSH_GATE_WAY_URL"));
        log.info("Injected URL: '{}'", pushgatewayUrl);
        log.info("Job: '{}'", jobName);

        if (pushgatewayUrl == null || pushgatewayUrl.trim().isEmpty()) {
            log.error("PushGateway URL is empty!");
            return;
        }

        try {
            URI.create(pushgatewayUrl); // URI 유효성 검사
            log.info("PushGateway URL is valid: {}", pushgatewayUrl);
        } catch (Exception e) {
            log.error("Invalid PushGateway URL: {}", pushgatewayUrl, e);
        }
        log.info("=================================");
    }

    @Scheduled(fixedRateString = "${management.prometheus.metrics.export.pushgateway.push-rate:5000}")
    public void pushMetrics() {
        if (pushgatewayUrl == null || pushgatewayUrl.trim().isEmpty()) {
            log.warn("PushGateway URL not configured, skipping push");
            return;
        }

        try {
            String metrics = prometheusMeterRegistry.scrape();
            String targetUrl = pushgatewayUrl + "/metrics/job/" + jobName;

            log.debug("Pushing to: {}", targetUrl);

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(targetUrl))
                    .header("Content-Type", "text/plain; version=0.0.4")
                    .timeout(Duration.ofSeconds(10))
                    .POST(HttpRequest.BodyPublishers.ofString(metrics))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                log.debug("Metrics pushed successfully");
            } else {
                log.warn("Push failed with status: {}", response.statusCode());
            }
        } catch (Exception e) {
            log.error("Failed to push metrics to {}: {}", pushgatewayUrl, e.getMessage());
        }
    }
}