package org.jmedina.jtetris.api.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import com.google.common.collect.ImmutableList;

/**
 * @author Jorge Medina
 *
 */
@EnableWebFluxSecurity
@Configuration
public class SecurityConfig {

	@Value("${api.cors.origin}")
	private String[] corsOrigin;

	@Bean
	SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
		http.csrf(csrf -> csrf.disable());
		http.cors(Customizer.withDefaults());
		return http.build();
	}

	@Bean
	CorsWebFilter corsConfigurationSource() {
		final CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(Arrays.asList(corsOrigin));
		configuration.setAllowedMethods(ImmutableList.of("*"));
		configuration.setAllowCredentials(false);
		configuration.setAllowedHeaders(ImmutableList.of("*"));
		final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return new CorsWebFilter(source);
	}
}