package org.jmedina.jtetris.api.controller;

import java.time.Duration;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jmedina.jtetris.api.client.EngineClient;
import org.jmedina.jtetris.api.model.BoardOperation;
import org.jmedina.jtetris.api.model.FigureOperation;
import org.jmedina.jtetris.api.model.Message;
import org.jmedina.jtetris.api.publisher.BoardPublisher;
import org.jmedina.jtetris.api.publisher.FigurePublisher;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
@RequestMapping(value = "/game")
public class TetrisController {

	private final Logger logger = LogManager.getLogger(this.getClass());
	private final FigurePublisher figurePublisher;
	private final BoardPublisher boardPublisher;
	private final EngineClient engineClient;
	private final boolean showHeaders = false;

	@GetMapping(value = "/hello", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Mono<Message>> hello() {
		this.logger.debug("===> TetrisController.hello()");
		try {
			return ResponseEntity.status(HttpStatus.OK)
					.body(Mono.just(new Message("Hello from api reactive!!!")).timeout(Duration.ofSeconds(3)));
		} catch (Exception e) {
			this.logger.error("=*=> ERROR: ", e);
			return ResponseEntity.internalServerError().body(Mono.empty());
		}
	}

	@GetMapping(value = "/isUp", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Mono<Boolean>> isUp() {
		this.logger.debug("===> TetrisController.isUp()");
		try {
			return ResponseEntity.status(HttpStatus.OK).body(this.engineClient.isUp().timeout(Duration.ofSeconds(3)));
		} catch (Exception e) {
			this.logger.error("=*=> ERROR: ", e);
			return ResponseEntity.internalServerError().body(Mono.empty());
		}
	}

	@PostMapping(value = "/stop", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Mono<Boolean>> stop() {
		this.logger.debug("===> TetrisController.stop()");
		try {
			this.figurePublisher.stop();
			this.boardPublisher.stop();
			return ResponseEntity.status(HttpStatus.OK).body(this.engineClient.stop().timeout(Duration.ofSeconds(3)));
		} catch (Exception e) {
			this.logger.error("=*=> ERROR: ", e);
			return ResponseEntity.internalServerError().body(Mono.just(false).timeout(Duration.ofSeconds(3)));
		}
	}

	@GetMapping(value = "/getFigureConversation", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public ResponseEntity<Flux<FigureOperation>> getFigureConversation(ServerWebExchange exchange) {
		this.logger.debug("===> TetrisController.getFigureConversation()");
		exchange.getResponse().getHeaders().addIfAbsent("Connection", "keep-alive");
		exchange.getResponse().getHeaders().addIfAbsent("Keep-Alive", "timeout=3600");
		if (this.showHeaders) {
			printHeaders(exchange);
		}
		Flux<FigureOperation> fluxOfFigures = null;
		try {
			fluxOfFigures = Flux.from(this.figurePublisher).doOnNext(figure -> {
				this.logger.debug("===> API - Flux.from.figurePublisher - NEXT = " + figure);
			}).doOnComplete(() -> {
				this.logger.debug("===> API - Flux.from.figurePublisher - COMPLETE!");
			}).doOnCancel(() -> {
				this.logger.debug("===> API - Flux.from.figurePublisher - CANCEL!");
			}).doOnTerminate(() -> {
				this.logger.debug("===> API - Flux.from.figurePublisher - TERMINATE!");
			}).doOnError(e -> {
				this.logger.error("==*=> ERROR - Flux.from.figurePublisher =", e);
			}).onErrorResume(e -> {
				this.logger.error("==*=> ERROR - Flux.from.figurePublisher =", e);
				return Flux.<FigureOperation>empty();
			});
			return ResponseEntity.status(HttpStatus.OK).body(fluxOfFigures.timeout(Duration.ofHours(1)));
		} catch (Exception e) {
			this.logger.error("=*=> ERROR: ", e);
			return ResponseEntity.internalServerError().body(Flux.<FigureOperation>empty());
		}
	}

	@GetMapping(value = "/getBoardConversation", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public ResponseEntity<Flux<BoardOperation>> getBoardConversation(ServerWebExchange exchange) {
		this.logger.debug("===> TetrisController.getBoardConversation()");
		exchange.getResponse().getHeaders().addIfAbsent("Connection", "keep-alive");
		exchange.getResponse().getHeaders().addIfAbsent("Keep-Alive", "timeout=3600");
		Flux<BoardOperation> fluxOfBoards = null;
		try {
			fluxOfBoards = Flux.from(this.boardPublisher).doOnNext(board -> {
				this.logger.debug("===> API - Flux.from.boardPublisher - NEXT = " + board);
			}).doOnComplete(() -> {
				this.logger.debug("===> API - Flux.from.boardPublisher - COMPLETE!");
			}).doOnCancel(() -> {
				this.logger.debug("===> API - Flux.from.boardPublisher - CANCEL!");
			}).doOnTerminate(() -> {
				this.logger.debug("===> API - Flux.from.boardPublisher - TERMINATE!");
			}).doOnError(e -> {
				this.logger.error("==*=> ERROR - Flux.from.boardPublisher =", e);
			}).onErrorResume(e -> {
				this.logger.error("==*=> ERROR - Flux.from.boardPublisher =", e);
				return Flux.<BoardOperation>empty();
			});
			;
			return ResponseEntity.status(HttpStatus.OK).body(fluxOfBoards.timeout(Duration.ofHours(1)));
		} catch (Exception e) {
			this.logger.error("=*=> ERROR: ", e);
			return ResponseEntity.internalServerError().body(Flux.<BoardOperation>empty());
		}
	}

	@PostMapping(value = "/moveRight", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Mono<Boolean>> moveRight() {
		this.logger.debug("===> TetrisController.moveRight()");
		try {
			return ResponseEntity.status(HttpStatus.OK)
					.body(this.engineClient.moveRight().timeout(Duration.ofSeconds(3)));
		} catch (Exception e) {
			this.logger.error("=*=> ERROR: ", e);
			return ResponseEntity.internalServerError().body(Mono.just(false).timeout(Duration.ofSeconds(3)));
		}
	}

	@PostMapping(value = "/moveLeft", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Mono<Boolean>> moveLeft() {
		this.logger.debug("===> TetrisController.moveLeft()");
		try {
			return ResponseEntity.status(HttpStatus.OK)
					.body(this.engineClient.moveLeft().timeout(Duration.ofSeconds(3)));
		} catch (Exception e) {
			this.logger.error("=*=> ERROR: ", e);
			return ResponseEntity.internalServerError().body(Mono.just(false).timeout(Duration.ofSeconds(3)));
		}
	}

	@PostMapping(value = "/rotateRight", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Mono<Boolean>> rotateRight() {
		this.logger.debug("===> EngineController.rotateRight()");
		try {
			return ResponseEntity.status(HttpStatus.OK)
					.body(this.engineClient.rotateRight().timeout(Duration.ofSeconds(3)));
		} catch (Exception e) {
			this.logger.error("=*=> ERROR: ", e);
			return ResponseEntity.internalServerError().body(Mono.just(false).timeout(Duration.ofSeconds(3)));
		}
	}

	@PostMapping(value = "/rotateLeft", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Mono<Boolean>> rotateLeft() {
		this.logger.debug("===> EngineController.rotateLeft()");
		try {
			return ResponseEntity.status(HttpStatus.OK)
					.body(this.engineClient.rotateLeft().timeout(Duration.ofSeconds(3)));
		} catch (Exception e) {
			this.logger.error("=*=> ERROR: ", e);
			return ResponseEntity.internalServerError().body(Mono.just(false).timeout(Duration.ofSeconds(3)));
		}
	}

	@PostMapping(value = "/bottomDown", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Mono<Boolean>> bottomDown() {
		this.logger.debug("===> EngineController.bottomDown()");
		try {
			return ResponseEntity.status(HttpStatus.OK)
					.body(this.engineClient.bottomDown().timeout(Duration.ofSeconds(3)));
		} catch (Exception e) {
			this.logger.error("=*=> ERROR: ", e);
			return ResponseEntity.internalServerError().body(Mono.just(false).timeout(Duration.ofSeconds(3)));
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