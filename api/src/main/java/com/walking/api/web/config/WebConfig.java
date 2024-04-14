package com.walking.api.web.config;

import com.walking.api.security.config.CorsConfigurationSourceProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

	private final CorsConfigurationSourceProperties corsProperties;

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry
				.addMapping(corsProperties.getPathPattern())
				// .allowedOriginPatterns(corsProperties.getOriginPattern())
				.allowedOrigins(corsProperties.getOriginPatterns())
				.allowedMethods(corsProperties.getAllowedMethods())
				.allowedHeaders(corsProperties.getAllowedHeaders())
				.allowCredentials(corsProperties.getAllowCredentials())
				.exposedHeaders(corsProperties.getExposedHeaders())
				.maxAge(corsProperties.getMaxAge());
	}
}
