package org.jmedina.jtetris.api.service.impl;

import java.time.Duration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jmedina.jtetris.api.service.ConversationService;
import org.jmedina.jtetris.api.util.BoardDeserializer;
import org.jmedina.jtetris.api.util.FigureDeserializer;
import org.jmedina.jtetris.common.model.BoardOperation;
import org.jmedina.jtetris.common.model.FigureOperation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

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
	private final ObjectMapper mapper = new ObjectMapper();
	private final SimpleModule module = new SimpleModule();

	@Value("${api.engine.base-url}")
	private String engineBaseUrl;

	private WebClient clientForConversations;

	@PostConstruct
	private void init() {
		module.addDeserializer(FigureOperation.class, new FigureDeserializer());
		module.addDeserializer(BoardOperation.class, new BoardDeserializer());
		mapper.registerModule(module);
		ExchangeStrategies exchangeStrategies = ExchangeStrategies.builder()
				.codecs(configurer -> configurer.defaultCodecs().jackson2JsonDecoder(new Jackson2JsonDecoder(mapper)))
				.build();

		int timeout = 3600000;
		ConnectionProvider connProvider = ConnectionProvider.builder("connectionProviderForConversationsApi")
				.maxConnections(2).pendingAcquireMaxCount(-1).pendingAcquireTimeout(Duration.ofHours(1))
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
				.exchangeStrategies(exchangeStrategies).build();
	}

	public Flux<FigureOperation> getFigureConversation() {
		return this.clientForConversations.get().uri("/getFigureConversation").retrieve()
				.bodyToFlux(FigureOperation.class);
	}

	public Flux<BoardOperation> getBoardConversation() {
		return this.clientForConversations.get().uri("/getBoardConversation").retrieve()
				.bodyToFlux(BoardOperation.class);
	}
}
