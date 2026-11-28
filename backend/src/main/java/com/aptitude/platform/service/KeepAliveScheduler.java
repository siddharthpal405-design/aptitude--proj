package com.aptitude.platform.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * In-app keep-alive bot that pings the configured public health URL every 14 minutes (840,000 ms).
 * Pairs with the external GitHub Actions cron workflow for Render free-tier uptime.
 */
@Service
@ConditionalOnProperty(name = "keepalive.enabled", havingValue = "true")
public class KeepAliveScheduler {

    private static final Logger log = LoggerFactory.getLogger(KeepAliveScheduler.class);

    private final HttpClient httpClient;
    private final String baseUrl;

    public KeepAliveScheduler(@Value("${keepalive.url:}") String baseUrl) {
        this.baseUrl = baseUrl == null ? "" : baseUrl.trim().replaceAll("/+$", "");
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(15))
                .build();
    }

    @Scheduled(
            initialDelayString = "${keepalive.interval-ms:840000}",
            fixedDelayString = "${keepalive.interval-ms:840000}"
    )
    public void pingSelf() {
        if (baseUrl.isBlank()) {
            return;
        }

        String healthUrl = baseUrl.endsWith("/api/health") ? baseUrl : baseUrl + "/api/health";
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(healthUrl))
                    .timeout(Duration.ofSeconds(20))
                    .header("User-Agent", "AptiMore-KeepAlive-Bot/1.0")
                    .GET()
                    .build();

            HttpResponse<Void> response = httpClient.send(request, HttpResponse.BodyHandlers.discarding());
            log.info("Keep-alive ping to {} -> HTTP {}", healthUrl, response.statusCode());
        } catch (Exception ex) {
            log.warn("Keep-alive ping to {} failed: {}", healthUrl, ex.getMessage());
        }
    }
}
