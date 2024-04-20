package org.jmedina.jtetris.gateway.filter;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.gateway.config.HttpClientProperties;
import org.springframework.cloud.gateway.filter.NettyRoutingFilter;
import org.springframework.cloud.gateway.filter.headers.HttpHeadersFilter;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import io.netty.channel.ChannelOption;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

/**
 * @author Jorge Medina
 *
 */
@Component
public class CustomNettyRoutingFilter extends NettyRoutingFilter {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private final boolean showHeaders = false;

	private String[] conversationPaths = new String[] { "/api/game/getFigureConversation",
			"/api/game/getBoardConversation", "/engine/getFigureConversation", "/engine/getBoardConversation",
			"/figures/getFigureConversation", "/engine/getNextFigureConversation" };
	private List<String> conversationPathsList = Arrays.asList(conversationPaths);

	private HttpClient httpClientForConversations;

	public CustomNettyRoutingFilter(HttpClient httpClient,
			ObjectProvider<List<HttpHeadersFilter>> headersFiltersProvider, HttpClientProperties properties) {
		super(httpClient, headersFiltersProvider, properties);
		int timeout = 3600000;
		ConnectionProvider connProvider = ConnectionProvider.builder("connectionProviderForConversationsGateway")
				.maxConnections(200).pendingAcquireMaxCount(-1).pendingAcquireTimeout(Duration.ofHours(1))
				.maxIdleTime(Duration.ofHours(1)).maxLifeTime(Duration.ofHours(1)).build();
		this.httpClientForConversations = HttpClient.create(connProvider).option(ChannelOption.AUTO_CLOSE, false)
				.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, timeout).option(ChannelOption.SO_KEEPALIVE, true)
				.keepAlive(true).responseTimeout(Duration.ofMillis(timeout))
				.headers(headers -> headers.set("Content-Type", "text/event-stream").set("Cache-Control", "no-cache")
						.set("Connection", "keep-alive").set("Keep-Alive", "timeout=3600"))
				.doOnConnected(conn -> {
					this.logger.debug("===============================> httpClient.doOnConnected - conn.isPersistent ="
							+ conn.isPersistent());
				}).doOnDisconnected(conn -> {
					this.logger.debug("===============================> httpClient.doOnDisconnected");
				});
	}

	@Override
	protected HttpClient getHttpClient(Route route, ServerWebExchange exchange) {
		this.logger.debug("==> CustomNettyRoutingFilter.getHttpClient()");
		if (this.showHeaders) {
			printHeaders(exchange);
		}
		String uri = route.getUri().toString();
		String path = exchange.getRequest().getPath().toString();
		this.logger.debug("=========> uri=" + uri);
		this.logger.debug("=========> path=" + path);
		boolean matchConversation = conversationPathsList.stream().anyMatch(p -> StringUtils.equals(p, path));
		if (matchConversation) {
			this.logger.debug("=========> matchConversation!");
			exchange.getResponse().getHeaders().addIfAbsent("Connection", "keep-alive");
			exchange.getResponse().getHeaders().addIfAbsent("Keep-Alive", "timeout=3600");
			return this.httpClientForConversations;
		}

		exchange.getResponse().getHeaders().addIfAbsent("authorization", getAuthHeader(exchange));
		return super.getHttpClient(route, exchange);
	}

	private void printHeaders(ServerWebExchange exchange) {
		this.logger.info("=========> HEADERS!!!");
		HttpHeaders headers = exchange.getRequest().getHeaders();
		Set<String> keys = headers.keySet();
		keys.stream().forEach(key -> {
			List<String> values = headers.get(key);
			if (values != null) {
				values.stream().forEach(v -> {
					this.logger.info("=========> header= {}: {}", key, v);
				});
			}
		});
		this.logger.info("=========> END HEADERS!!!");
	}

	private String getAuthHeader(ServerWebExchange exchange) {
		List<String> values = exchange.getRequest().getHeaders().get("authorization");
		if (Objects.nonNull(values) && !values.isEmpty()) {
			return values.get(0);
		}
		return null;
	}

}
