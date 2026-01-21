package com.eacore.trace.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Configuration properties for EaCore trace feature.
 * Allows enabling/disabling the feature and excluding URL patterns.
 */
@ConfigurationProperties(prefix = "eacore.trace")
public class EaCoreTraceProperties {

    /**
     * Enable or disable the trace feature. Defaults to true.
     */
    private boolean enabled = true;

    /**
     * URL patterns to be excluded from tracing.
     * Defaults to common patterns like swagger and actuator.
     */
    private List<String> excludePatterns = new ArrayList<>(Arrays.asList("/swagger-resources/**", "/webjars/**", "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html", "/actuator/**"));

    // Getters and Setters
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public List<String> getExcludePatterns() { return excludePatterns; }
    public void setExcludePatterns(List<String> excludePatterns) { this.excludePatterns = excludePatterns; }
}