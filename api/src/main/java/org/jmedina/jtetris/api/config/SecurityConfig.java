package org.jmedina.jtetris.api.config;

import static org.springframework.security.config.Customizer.withDefaults;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.security.web.server.authentication.HttpStatusServerEntryPoint;
import org.springframework.security.web.server.authentication.logout.HttpStatusReturningServerLogoutSuccessHandler;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher.MatchResult;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.server.ServerWebExchange;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;

import lombok.NoArgsConstructor;
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

	private String[] securePages = new String[] { "/api", "/api/", "/api/index.html", "/api/user/resource" };
	private String[] secureTypes = new String[] { ".js", ".css", ".ico" };

	private List<String> securePagesList = Arrays.asList(securePages);
	private List<String> secureTypesList = Arrays.asList(secureTypes);

	@Bean
	MapReactiveUserDetailsService userDetailsService() {
		// Username: user
		// Password: s3cr3t!!!
		return new MapReactiveUserDetailsService(User.withUsername("user")
				.password("{bcrypt}$2a$10$Lmi5Y5kkU//VUyLJSgeMm.S/kItz0COSN2fVF7K.Ehv.a8qQTxrpi").roles("USER")
				.build());
	}

	@Bean
	SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
		//return http.cors(cors -> cors.disable()).csrf(csrf -> csrf.disable()).build();
		return http.cors(withDefaults()).formLogin(formLogin -> formLogin.disable())
				.securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
				.authorizeExchange(exchanges -> exchanges.matchers(this::blockUnsecured).permitAll())
				.authorizeExchange(exchanges -> exchanges.anyExchange().authenticated())
				.httpBasic(httpBasic -> httpBasic.authenticationEntryPoint(new NoPopupBasicAuthenticationEntryPoint()))
				.exceptionHandling(
						eh -> eh.authenticationEntryPoint(new HttpStatusServerEntryPoint(HttpStatus.FORBIDDEN)))
				.logout(logout -> logout.logoutUrl("/logout")
						.logoutSuccessHandler(new HttpStatusReturningServerLogoutSuccessHandler(HttpStatus.OK)))
				.csrf(csrf -> csrf.disable()).build();
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

	private Mono<MatchResult> blockUnsecured(final ServerWebExchange exchange) {
		URI uri = exchange.getRequest().getURI();

		boolean valid = secureTypesList.stream().anyMatch(type -> StringUtils.endsWith(uri.getPath(), type))
				|| securePagesList.stream().anyMatch(p -> p.equalsIgnoreCase(uri.getPath()));

		return valid ? MatchResult.match() : MatchResult.notMatch();
	}

	public class NoPopupBasicAuthenticationEntryPoint implements ServerAuthenticationEntryPoint {

		@Override
		public Mono<Void> commence(ServerWebExchange exchange, AuthenticationException ex) {
			ServerHttpResponse response = exchange.getResponse();
			response.setStatusCode(HttpStatus.UNAUTHORIZED);
			return new AuthFailureHandler().formatResponse(response);
		}
	}

	@NoArgsConstructor
	public class AuthFailureHandler {
		public Mono<Void> formatResponse(ServerHttpResponse response) {
			response.getHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
			ObjectMapper mapper = new ObjectMapper();
			ApiResponse apiResponse = new ApiResponse(response.getStatusCode().value(), "Access Denied !!!");
			StringBuilder json = new StringBuilder();
			try {
				json.append(mapper.writeValueAsString(apiResponse));
			} catch (JsonProcessingException jsonProcessingException) {
			}

			String responseBody = json.toString();
			byte[] bytes = responseBody.getBytes(StandardCharsets.UTF_8);
			DataBuffer buffer = response.bufferFactory().wrap(bytes);
			return response.writeWith(Mono.just(buffer));
		}
	}

	public class ApiResponse {

		int statusCode;
		String message;

		public ApiResponse(int statusCode, String message) {
			this.statusCode = statusCode;
			this.message = message;
		}

	}
}
