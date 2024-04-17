package org.jmedina.jtetris.figures.controller;

import java.time.Duration;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jmedina.jtetris.common.model.FigureOperation;
import org.jmedina.jtetris.figures.model.Message;
import org.jmedina.jtetris.figures.publisher.FigurePublisher;
import org.jmedina.jtetris.figures.publisher.NextFigurePublisher;
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
	private final FigurePublisher figurePublisher;
	private final NextFigurePublisher nextFigurePublisher;
	private final boolean showHeaders = false;

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
	public Mono<Boolean> isUp() {
		this.logger.debug("===> TetrisController.isUp()");
		try {
			return Mono.just(true).timeout(Duration.ofSeconds(3));
		} catch (Exception e) {
			this.logger.error("=*=> ERROR: ", e);
			return Mono.empty();
		}
	}

	@PostMapping(value = "/stop", produces = MediaType.APPLICATION_JSON_VALUE)
	public Mono<Boolean> stop() {
		this.logger.debug("===> FigureController.stop()");
		try {
			this.figurePublisher.stop();
			this.nextFigurePublisher.stop();
			return Mono.just(true).timeout(Duration.ofSeconds(3));
		} catch (Exception e) {
			this.logger.error("=*=> ERROR: ", e);
			return Mono.just(false).timeout(Duration.ofSeconds(3));
		}
	}

	@GetMapping(value = "/getFigureConversation", produces = MediaType.APPLICATION_JSON_VALUE)
	public Flux<FigureOperation> getFigureConversation(ServerWebExchange exchange) {
		this.logger.debug("===> FigureController.getFigureConversation()");
		exchange.getResponse().getHeaders().addIfAbsent("Connection", "keep-alive");
		exchange.getResponse().getHeaders().addIfAbsent("Keep-Alive", "timeout=3600");
		if (this.showHeaders) {
			printHeaders(exchange);
		}
		Flux<FigureOperation> fluxOfFigures = null;
		try {
			fluxOfFigures = Flux.from(this.figurePublisher).doOnNext(figure -> {
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
				return Flux.<FigureOperation>empty();
			});
			return fluxOfFigures.timeout(Duration.ofHours(1));
		} catch (Exception e) {
			this.logger.error("=*=> ERROR: ", e);
			return Flux.<FigureOperation>empty();
		}
	}

	private void printHeaders(ServerWebExchange exchange) {
		this.logger.debug("=========> HEADERS!!!");
		HttpHeaders headers = exchange.getRequest().getHeaders();
		Set<String> keys = headers.keySet();
		keys.stream().forEach(key -> {
			List<String> values = headers.get(key);
			if (values != null) {
				values.stream().forEach(v -> {
					this.logger.debug("=========> header= {}: {}", key, v);
				});
			}
		});
		this.logger.debug("=========> END HEADERS!!!");
	}

}