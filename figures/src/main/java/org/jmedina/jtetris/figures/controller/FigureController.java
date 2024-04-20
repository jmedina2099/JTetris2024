package org.jmedina.jtetris.figures.controller;

import java.security.Principal;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.javatuples.Pair;
import org.jmedina.jtetris.common.model.FigureOperation;
import org.jmedina.jtetris.figures.figure.BoxDB;
import org.jmedina.jtetris.figures.figure.FigureDB;
import org.jmedina.jtetris.figures.model.Message;
import org.jmedina.jtetris.figures.publisher.FigurePublisher;
import org.jmedina.jtetris.figures.publisher.NextFigurePublisher;
import org.jmedina.jtetris.figures.service.ConversationService;
import org.jmedina.jtetris.figures.service.FigureService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author Jorge Medina
 *
 */
@RequiredArgsConstructor
@RestController
@RequestMapping
public class FigureController {

	private final Logger logger = LogManager.getLogger(this.getClass());
	private final FigureService figureService;
	private final ConversationService conversationService;
	private final boolean showHeaders = false;

	private final Map<String, Pair<FigurePublisher, NextFigurePublisher>> mapSession = new HashMap<>();

	@GetMapping("/hello")
	public Mono<Message> hello() {
		this.logger.debug("===> FigureController.hello()");
		try {
			return Mono.just(new Message("Hello from figures reactive!!!"));
		} catch (Exception e) {
			this.logger.error("=*=> ERROR: ", e);
			return Mono.empty();
		}
	}

	@GetMapping(value = "/isUp", produces = MediaType.APPLICATION_JSON_VALUE)
	public Mono<Boolean> isUp(Principal user, ServerWebExchange exchange) {
		this.logger.debug("===> TetrisController.isUp()");
		this.logger.debug("===> FIGURES - USERNAME = {}", Objects.nonNull(user) ? user.getName() : user);
		if (this.showHeaders) {
			printHeaders(exchange);
		}
		try {
			return Mono.just(true).timeout(Duration.ofSeconds(3));
		} catch (Exception e) {
			this.logger.error("=*=> ERROR: ", e);
			return Mono.empty();
		}
	}

	@PostMapping(value = "/start", produces = MediaType.APPLICATION_JSON_VALUE)
	public Mono<Boolean> start(Principal user, ServerWebExchange exchange) {
		this.logger.debug("===> EngineController.start()");
		String auth = getAuthHeader(exchange);
		try {
			if (Objects.nonNull(user)) {
				NextFigurePublisher nextFigurePublisher = new NextFigurePublisher(auth, this.conversationService);
				FigurePublisher figurePublisher = new FigurePublisher(this.figureService, nextFigurePublisher);
				mapSession.put(user.getName(), Pair.with(figurePublisher, nextFigurePublisher));
			}
			return Mono.just(true).timeout(Duration.ofSeconds(3));
		} catch (Exception e) {
			this.logger.error("=*=> ERROR: ", e);
			return Mono.just(false).timeout(Duration.ofSeconds(3));
		}
	}

	@PostMapping(value = "/stop", produces = MediaType.APPLICATION_JSON_VALUE)
	public Mono<Boolean> stop(Principal user) {
		this.logger.debug("===> FigureController.stop()");
		try {
			if (Objects.nonNull(user)) {
				Pair<FigurePublisher, NextFigurePublisher> publishers = mapSession.get(user.getName());
				if (Objects.nonNull(publishers)) {
					publishers.getValue0().stop();
					publishers.getValue1().stop();
				}
			}
			return Mono.just(true).timeout(Duration.ofSeconds(3));
		} catch (Exception e) {
			this.logger.error("=*=> ERROR: ", e);
			return Mono.just(false).timeout(Duration.ofSeconds(3));
		}
	}

	@GetMapping(value = "/getFigureConversation", produces = MediaType.APPLICATION_JSON_VALUE)
	public Flux<FigureOperation<BoxDB, FigureDB<BoxDB>>> getFigureConversation(Principal user,
			ServerWebExchange exchange) {
		this.logger.debug("===> FigureController.getFigureConversation()");
		this.logger.debug("===> FIGURES - USERNAME = {}", Objects.nonNull(user) ? user.getName() : "");
		exchange.getResponse().getHeaders().addIfAbsent("Connection", "keep-alive");
		exchange.getResponse().getHeaders().addIfAbsent("Keep-Alive", "timeout=3600");
		if (this.showHeaders) {
			printHeaders(exchange);
		}
		FigurePublisher figurePublisher = null;
		if (Objects.nonNull(user)) {
			Pair<FigurePublisher, NextFigurePublisher> publishers = mapSession.get(user.getName());
			if (Objects.nonNull(publishers)) {
				figurePublisher = publishers.getValue0();
			}
		}
		Flux<FigureOperation<BoxDB, FigureDB<BoxDB>>> fluxOfFigures = null;
		try {
			fluxOfFigures = Flux.from(figurePublisher).doOnNext(figure -> {
				this.logger.debug("===> FIGURES - Flux.from.figurePublisher - NEXT = " + figure);
			}).doOnComplete(() -> {
				this.logger.debug("===> FIGURES - Flux.from.figurePublisher - COMPLETE!");
			}).doOnCancel(() -> {
				this.logger.debug("===> FIGURES - Flux.from.figurePublisher - CANCEL!");
			}).doOnTerminate(() -> {
				this.logger.debug("===> FIGURES - Flux.from.figurePublisher - TERMINATE!");
			}).doOnError(e -> {
				this.logger.error("==*=> ERROR - Flux.from.figurePublisher =", e);
			}).onErrorResume(e -> {
				this.logger.error("==*=> ERROR - Flux.from.figurePublisher =", e);
				return Flux.<FigureOperation<BoxDB, FigureDB<BoxDB>>>empty();
			});
			return fluxOfFigures.timeout(Duration.ofHours(1));
		} catch (Exception e) {
			this.logger.error("=*=> ERROR: ", e);
			return Flux.<FigureOperation<BoxDB, FigureDB<BoxDB>>>empty();
		}
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