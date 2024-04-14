package com.walking.api.security.config;

import com.walking.api.security.authentication.token.TokenAuthProvider;
import com.walking.api.security.filter.exception.TokenInvalidExceptionHandlerFilter;
import com.walking.api.security.filter.token.TokenAuthenticationFilter;
import com.walking.api.security.handler.DelegatedAccessDeniedHandler;
import com.walking.api.security.handler.DelegatedAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.OncePerRequestFilter;

@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

	private final DelegatedAuthenticationEntryPoint authenticationEntryPoint;
	private final DelegatedAccessDeniedHandler accessDeniedHandler;
	private final TokenAuthProvider tokenAuthProvider;
	private final CorsConfigurationSourceProperties corsProperties;

	@Bean
	@Profile("!prod")
	public SecurityFilterChain localSecurityFilterChain(HttpSecurity http) throws Exception {
		http.csrf().disable();
		http.formLogin().disable();
		http.httpBasic().disable();
		http.cors().configurationSource(corsConfigurationSource());

		http.authorizeRequests().antMatchers("/api/v1/**").authenticated().anyRequest().denyAll();

		http.addFilterBefore(
				getTokenInvalidExceptionHandlerFilter(), AbstractPreAuthenticatedProcessingFilter.class);
		http.addFilterAt(
				generateAuthenticationFilter(), AbstractPreAuthenticatedProcessingFilter.class);

		http.exceptionHandling()
				.authenticationEntryPoint(authenticationEntryPoint)
				.accessDeniedHandler(accessDeniedHandler);

		http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
		return http.build();
	}

	@Bean
	@Profile(value = "prod")
	public SecurityFilterChain prodSecurityFilterChain(HttpSecurity http) throws Exception {
		http.csrf().disable();
		http.formLogin().disable();
		http.httpBasic().disable();
		http.cors().configurationSource(corsConfigurationSource());

		http.authorizeRequests().antMatchers("/api/v1/**").authenticated().anyRequest().denyAll();

		http.addFilterBefore(
				getTokenInvalidExceptionHandlerFilter(), AbstractPreAuthenticatedProcessingFilter.class);
		http.addFilterAt(
				generateAuthenticationFilter(), AbstractPreAuthenticatedProcessingFilter.class);

		http.exceptionHandling()
				.authenticationEntryPoint(authenticationEntryPoint)
				.accessDeniedHandler(accessDeniedHandler);

		http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
		return http.build();
	}

	public TokenAuthenticationFilter generateAuthenticationFilter() {
		TokenAuthenticationFilter tokenAuthenticationFilter = new TokenAuthenticationFilter();
		tokenAuthenticationFilter.setAuthenticationManager(new ProviderManager(tokenAuthProvider));
		return tokenAuthenticationFilter;
	}

	public OncePerRequestFilter getTokenInvalidExceptionHandlerFilter() {
		return new TokenInvalidExceptionHandlerFilter();
	}

	@Bean
	@Profile("!prod")
	public WebSecurityCustomizer localWebSecurityFilterIgnoreCustomizer() {
		return web ->
				web.ignoring()
						.antMatchers(
								HttpMethod.GET,
								"/actuator/health",
								"/error",
								"/docs/swagger-ui/*",
								"/swagger-ui/*",
								"/swagger-resources/**",
								"/v3/api-docs/**",
								"/openapi3.yaml",
								"/reports/**") // TODO: ADD
						.antMatchers(HttpMethod.POST); // TODO: ADD
	}

	@Bean
	@Profile("prod")
	public WebSecurityCustomizer prodWebSecurityFilterIgnoreCustomizer() {
		return web ->
				web.ignoring()
						.antMatchers(
								HttpMethod.GET,
								"/actuator/health",
								"/error",
								"/docs/swagger-ui/*",
								"/swagger-ui/*",
								"/swagger-resources/**",
								"/v3/api-docs/**",
								"/openapi3.yaml",
								"/reports/**") // TODO: ADD
						.antMatchers(HttpMethod.POST); // TODO: ADD
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();

		configuration.addAllowedOriginPattern(corsProperties.getOriginPatterns());
		configuration.addAllowedHeader(corsProperties.getAllowedHeaders());
		configuration.addAllowedMethod(corsProperties.getAllowedMethods());
		configuration.setAllowCredentials(corsProperties.getAllowCredentials());

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration(corsProperties.getPathPattern(), configuration);
		return source;
	}
}
