package org.jmedina.jtetris.api.filter;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import reactor.core.publisher.Mono;

/**
 * @author Jorge Medina
 *
 */
@Component
public class HeaderFilter implements WebFilter {

	private final Logger logger = LogManager.getLogger(this.getClass());

	@Value("${api.cors.origin}")
	private String[] corsOrigin;

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
		this.logger.debug("===> doFilter = {} -- {}",
				Arrays.asList(exchange.getRequest().getHeaders().get("Origin")).toString(),
				Arrays.asList(corsOrigin).toString());
		final ServerHttpResponse response = exchange.getResponse();
		List<String> listOrigins = exchange.getRequest().getHeaders().get("Origin");
		if (Objects.nonNull(listOrigins)) {
			String origin = listOrigins.get(0);
			Arrays.asList(corsOrigin).stream().forEach(c -> {
				if (c.equals(origin)) {
					response.getHeaders().add("Access-Control-Allow-Origin", c);
				}
			});
		}
		response.getHeaders().add("Access-Control-Allow-Methods", "HEAD, GET, PUT, POST, DELETE, PATCH, OPTIONS");
		response.getHeaders().add("Access-Control-Max-Age", "3600");
		response.getHeaders().add("Access-Control-Allow-Headers", "*");
		try {
			return chain.filter(exchange);
		} catch (Exception e) {
			this.logger.error("=*=> ERROR: ", e);
			return Mono.<Void>empty();
		}
	}

}