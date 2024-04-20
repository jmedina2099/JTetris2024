package org.jmedina.jtetris.api.controller;

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
import org.jmedina.jtetris.api.client.EngineClient;
import org.jmedina.jtetris.api.model.Message;
import org.jmedina.jtetris.api.publisher.BoardPublisher;
import org.jmedina.jtetris.api.publisher.FigurePublisher;
import org.jmedina.jtetris.api.service.ConversationService;
import org.jmedina.jtetris.common.model.BoardOperation;
import org.jmedina.jtetris.common.model.BoxDTO;
import org.jmedina.jtetris.common.model.FigureDTO;
import org.jmedina.jtetris.common.model.FigureOperation;
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
	private final ConversationService conversationService;
	private final EngineClient engineClient;
	private final boolean showHeaders = false;

	private final Map<String, Pair<FigurePublisher, BoardPublisher>> mapSession = new HashMap<>();

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
	public ResponseEntity<Mono<Boolean>> isUp(Principal user, ServerWebExchange exchange) {
		this.logger.debug("===> TetrisController.isUp()");
		this.logger.debug("===> API - USERNAME = {}", Objects.nonNull(user) ? user.getName() : user);
		if (this.showHeaders) {
			printHeaders(exchange);
		}
		try {
			return ResponseEntity.status(HttpStatus.OK)
					.body(this.engineClient.isUp(getAuthHeader(exchange)).timeout(Duration.ofSeconds(3)));
		} catch (Exception e) {
			this.logger.error("=*=> ERROR: ", e);
			return ResponseEntity.internalServerError().body(Mono.empty());
		}
	}

	@PostMapping(value = "/start", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Mono<Boolean>> start(Principal user, ServerWebExchange exchange) {
		this.logger.debug("===> TetrisController.start()");
		String auth = getAuthHeader(exchange);
		try {
			if (Objects.nonNull(user)) {
				mapSession.put(user.getName(), Pair.with(new FigurePublisher(auth, conversationService),
						new BoardPublisher(auth, conversationService)));
			}
			return ResponseEntity.status(HttpStatus.OK)
					.body(this.engineClient.start(auth).timeout(Duration.ofSeconds(3)));
		} catch (Exception e) {
			this.logger.error("=*=> ERROR: ", e);
			return ResponseEntity.internalServerError().body(Mono.just(false).timeout(Duration.ofSeconds(3)));
		}
	}

	@PostMapping(value = "/stop", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Mono<Boolean>> stop(Principal user, ServerWebExchange exchange) {
		this.logger.debug("===> TetrisController.stop()");
		try {
			if (Objects.nonNull(user)) {
				Pair<FigurePublisher, BoardPublisher> publishers = mapSession.get(user.getName());
				if (Objects.nonNull(publishers)) {
					publishers.getValue0().stop();
					publishers.getValue1().stop();
				}
			}
			return ResponseEntity.status(HttpStatus.OK)
					.body(this.engineClient.stop(getAuthHeader(exchange)).timeout(Duration.ofSeconds(3)));
		} catch (Exception e) {
			this.logger.error("=*=> ERROR: ", e);
			return ResponseEntity.internalServerError().body(Mono.just(false).timeout(Duration.ofSeconds(3)));
		}
	}

	@GetMapping(value = "/getFigureConversation", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public ResponseEntity<Flux<FigureOperation<BoxDTO, FigureDTO<BoxDTO>>>> getFigureConversation(Principal user,
			ServerWebExchange exchange) {
		this.logger.debug("===> TetrisController.getFigureConversation()");
		this.logger.debug("===> API - USERNAME = {}", Objects.nonNull(user) ? user.getName() : user);
		exchange.getResponse().getHeaders().addIfAbsent("Connection", "keep-alive");
		exchange.getResponse().getHeaders().addIfAbsent("Keep-Alive", "timeout=3600");
		if (this.showHeaders) {
			printHeaders(exchange);
		}
		FigurePublisher figurePublisher = null;
		if (Objects.nonNull(user)) {
			Pair<FigurePublisher, BoardPublisher> publishers = mapSession.get(user.getName());
			if (Objects.nonNull(publishers)) {
				figurePublisher = publishers.getValue0();
			}
		}
		Flux<FigureOperation<BoxDTO, FigureDTO<BoxDTO>>> fluxOfFigures = null;
		try {
			fluxOfFigures = Flux.from(figurePublisher).doOnNext(figure -> {
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
				return Flux.<FigureOperation<BoxDTO, FigureDTO<BoxDTO>>>empty();
			});
			return ResponseEntity.status(HttpStatus.OK).body(fluxOfFigures.timeout(Duration.ofHours(1)));
		} catch (Exception e) {
			this.logger.error("=*=> ERROR: ", e);
			return ResponseEntity.internalServerError().body(Flux.<FigureOperation<BoxDTO, FigureDTO<BoxDTO>>>empty());
		}
	}

	@GetMapping(value = "/getBoardConversation", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public ResponseEntity<Flux<BoardOperation<BoxDTO>>> getBoardConversation(Principal user,
			ServerWebExchange exchange) {
		this.logger.debug("===> TetrisController.getBoardConversation()");
		exchange.getResponse().getHeaders().addIfAbsent("Connection", "keep-alive");
		exchange.getResponse().getHeaders().addIfAbsent("Keep-Alive", "timeout=3600");
		BoardPublisher boardPublisher = null;
		if (Objects.nonNull(user)) {
			Pair<FigurePublisher, BoardPublisher> publishers = mapSession.get(user.getName());
			if (Objects.nonNull(publishers)) {
				boardPublisher = publishers.getValue1();
			}
		}
		Flux<BoardOperation<BoxDTO>> fluxOfBoards = null;
		try {
			fluxOfBoards = Flux.from(boardPublisher).doOnNext(board -> {
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
				return Flux.<BoardOperation<BoxDTO>>empty();
			});
			;
			return ResponseEntity.status(HttpStatus.OK).body(fluxOfBoards.timeout(Duration.ofHours(1)));
		} catch (Exception e) {
			this.logger.error("=*=> ERROR: ", e);
			return ResponseEntity.internalServerError().body(Flux.<BoardOperation<BoxDTO>>empty());
		}
	}

	@PostMapping(value = "/moveRight", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Mono<Boolean>> moveRight(ServerWebExchange exchange) {
		this.logger.debug("===> TetrisController.moveRight()");
		try {
			return ResponseEntity.status(HttpStatus.OK)
					.body(this.engineClient.moveRight(getAuthHeader(exchange)).timeout(Duration.ofSeconds(3)));
		} catch (Exception e) {
			this.logger.error("=*=> ERROR: ", e);
			return ResponseEntity.internalServerError().body(Mono.just(false).timeout(Duration.ofSeconds(3)));
		}
	}

	@PostMapping(value = "/moveLeft", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Mono<Boolean>> moveLeft(ServerWebExchange exchange) {
		this.logger.debug("===> TetrisController.moveLeft()");
		try {
			return ResponseEntity.status(HttpStatus.OK)
					.body(this.engineClient.moveLeft(getAuthHeader(exchange)).timeout(Duration.ofSeconds(3)));
		} catch (Exception e) {
			this.logger.error("=*=> ERROR: ", e);
			return ResponseEntity.internalServerError().body(Mono.just(false).timeout(Duration.ofSeconds(3)));
		}
	}

	@PostMapping(value = "/rotateRight", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Mono<Boolean>> rotateRight(ServerWebExchange exchange) {
		this.logger.debug("===> EngineController.rotateRight()");
		try {
			return ResponseEntity.status(HttpStatus.OK)
					.body(this.engineClient.rotateRight(getAuthHeader(exchange)).timeout(Duration.ofSeconds(3)));
		} catch (Exception e) {
			this.logger.error("=*=> ERROR: ", e);
			return ResponseEntity.internalServerError().body(Mono.just(false).timeout(Duration.ofSeconds(3)));
		}
	}

	@PostMapping(value = "/rotateLeft", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Mono<Boolean>> rotateLeft(ServerWebExchange exchange) {
		this.logger.debug("===> EngineController.rotateLeft()");
		try {
			return ResponseEntity.status(HttpStatus.OK)
					.body(this.engineClient.rotateLeft(getAuthHeader(exchange)).timeout(Duration.ofSeconds(3)));
		} catch (Exception e) {
			this.logger.error("=*=> ERROR: ", e);
			return ResponseEntity.internalServerError().body(Mono.just(false).timeout(Duration.ofSeconds(3)));
		}
	}

	@PostMapping(value = "/bottomDown", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Mono<Boolean>> bottomDown(ServerWebExchange exchange) {
		this.logger.debug("===> EngineController.bottomDown()");
		try {
			return ResponseEntity.status(HttpStatus.OK)
					.body(this.engineClient.bottomDown(getAuthHeader(exchange)).timeout(Duration.ofSeconds(3)));
		} catch (Exception e) {
			this.logger.error("=*=> ERROR: ", e);
			return ResponseEntity.internalServerError().body(Mono.just(false).timeout(Duration.ofSeconds(3)));
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