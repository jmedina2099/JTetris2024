package org.jmedina.jtetris.figures.service.impl;

import java.time.Duration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jmedina.jtetris.figures.model.NextFigureOperation;
import org.jmedina.jtetris.figures.service.ConversationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import io.netty.channel.ChannelOption;
import jakarta.annotation.PostConstruct;
import reactor.core.publisher.Flux;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

/**
 * @author Jorge Medina
 *
 */
@Service
public class ConversationServiceImpl implements ConversationService {

	private final Logger logger = LogManager.getLogger(this.getClass());

	@Value("${figures.engine.base-url}")
	private String engineBaseUrl;

	private WebClient clientForConversations;

	@PostConstruct
	private void init() {
		int timeout = 3600000;
		ConnectionProvider connProvider = ConnectionProvider.builder("connectionProviderForConversationsFigures")
				.maxConnections(200).pendingAcquireMaxCount(-1).pendingAcquireTimeout(Duration.ofHours(1))
				.maxIdleTime(Duration.ofHours(1)).maxLifeTime(Duration.ofHours(1)).build();
		HttpClient httpClient = HttpClient.create(connProvider).baseUrl(this.engineBaseUrl)
				.option(ChannelOption.AUTO_CLOSE, false).option(ChannelOption.CONNECT_TIMEOUT_MILLIS, timeout)
				.option(ChannelOption.SO_KEEPALIVE, true).keepAlive(true).responseTimeout(Duration.ofMillis(timeout))
				.headers(headers -> headers.set("Content-Type", "text/event-stream").set("Cache-Control", "no-cache")
						.set("Connection", "keep-alive").set("Keep-Alive", "timeout=3600"))
				.doOnConnected(conn -> {
					this.logger.debug("===============================> httpClient.doOnConnected - conn.isPersistent ="
							+ conn.isPersistent());
				}).doOnDisconnected(conn -> {
					this.logger.debug("===============================> httpClient.doOnDisconnected");
				});
		this.clientForConversations = WebClient.builder().clientConnector(new ReactorClientHttpConnector(httpClient))
				.build();
	}

	public Flux<NextFigureOperation> getNextFigureConversation(String auth) {
		return this.clientForConversations.get().uri("/getNextFigureConversation")
				.headers(h -> h.addIfAbsent("authorization", auth)).retrieve().bodyToFlux(NextFigureOperation.class);
	}

}
