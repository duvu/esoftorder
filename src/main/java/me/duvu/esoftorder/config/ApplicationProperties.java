package me.duvu.esoftorder.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Properties specific to Esoft Order.
 * <p>
 * Properties are configured in the {@code application.yml} file.
 */
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
public class ApplicationProperties {}
