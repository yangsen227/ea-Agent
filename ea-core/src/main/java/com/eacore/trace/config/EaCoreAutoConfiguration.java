package com.eacore.trace.config;

import com.eacore.trace.controller.TraceController;
import com.eacore.trace.interceptor.TraceInterceptor;
import com.eacore.trace.service.TraceInfoRegistry;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration(proxyBeanMethods = false)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@EnableConfigurationProperties(EaCoreTraceProperties.class)
@ConditionalOnProperty(prefix = "eacore.trace", name = "enabled", havingValue = "true", matchIfMissing = true)
public class EaCoreAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public TraceInfoRegistry traceInfoRegistry() {
        return new TraceInfoRegistry();
    }

    @Bean
    @ConditionalOnMissingBean
    public TraceInterceptor traceInterceptor(TraceInfoRegistry traceInfoRegistry) {
        return new TraceInterceptor(traceInfoRegistry);
    }

    @Bean
    @ConditionalOnMissingBean
    public TraceController traceController(TraceInfoRegistry traceInfoRegistry) {
        return new TraceController(traceInfoRegistry);
    }

    @Configuration(proxyBeanMethods = false)
    static class WebMvcConfig implements WebMvcConfigurer {
        private final TraceInterceptor traceInterceptor;
        private final EaCoreTraceProperties properties;

        public WebMvcConfig(TraceInterceptor traceInterceptor, EaCoreTraceProperties properties) {
            this.traceInterceptor = traceInterceptor;
            this.properties = properties;
        }

        @Override
        public void addInterceptors(InterceptorRegistry registry) {
            registry.addInterceptor(traceInterceptor)
                    .addPathPatterns("/**")
                    .excludePathPatterns(properties.getExcludePatterns());
        }
    }
}