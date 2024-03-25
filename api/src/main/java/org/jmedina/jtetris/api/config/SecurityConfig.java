package org.jmedina.jtetris.api.config;

import static org.springframework.security.config.Customizer.withDefaults;

import java.net.URI;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher.MatchResult;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.server.ServerWebExchange;

import com.google.common.collect.ImmutableList;

import reactor.core.publisher.Mono;

/**
 * @author Jorge Medina
 *
 */
@EnableWebFluxSecurity
@Configuration
public class SecurityConfig {

	@Value("${api.cors.origin}")
	private String[] corsOrigin;

	@SuppressWarnings("deprecation")
	@Bean
	MapReactiveUserDetailsService userDetailsService() {
		UserDetails user = User.withDefaultPasswordEncoder().username("user").password("user").roles("USER").build();
		return new MapReactiveUserDetailsService(user);
	}

	@Bean
	SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
		return http.cors(withDefaults())
				.formLogin(formLogin -> formLogin.disable())
				.securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
				.authorizeExchange(exchanges -> exchanges.matchers(this::blockUnsecured).permitAll())
				.authorizeExchange(exchanges -> exchanges.anyExchange().authenticated()).httpBasic(withDefaults())
				.csrf(csrf -> csrf.disable()).build();
	}

	private Mono<MatchResult> blockUnsecured(final ServerWebExchange exchange) {
		URI uri = exchange.getRequest().getURI();
		boolean valid = "/api/index.html".equalsIgnoreCase(uri.getPath()) || "/api/".equalsIgnoreCase(uri.getPath())
				|| "/api/home".equalsIgnoreCase(uri.getPath()) || "/api/login".equalsIgnoreCase(uri.getPath())
				|| "/api/resource".equalsIgnoreCase(uri.getPath()) || uri.getPath().contains(".js")
				|| uri.getPath().contains(".css") || uri.getPath().contains(".ico");
		return valid ? MatchResult.match() : MatchResult.notMatch();
	}

	@Bean
	CorsWebFilter corsConfigurationSource() {
		final CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(Arrays.asList(corsOrigin));
		configuration.setAllowedMethods(ImmutableList.of("HEAD", "GET", "PUT", "POST", "DELETE", "PATCH", "OPTIONS"));
		configuration.setAllowedHeaders(ImmutableList.of("*"));
		configuration.setAllowCredentials(true);
		final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return new CorsWebFilter(source);
	}
}