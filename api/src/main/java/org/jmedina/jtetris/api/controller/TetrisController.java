package org.jmedina.jtetris.api.controller;

import java.time.Duration;

import org.jmedina.jtetris.api.client.EngineClient;
import org.jmedina.jtetris.api.model.BoardOperation;
import org.jmedina.jtetris.api.model.FigureOperation;
import org.jmedina.jtetris.api.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private final EngineClient engineClient;

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

	@PostMapping(value = "/start", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Mono<Boolean>> start() {
		this.logger.debug("===> TetrisController.start()");
		try {
			return ResponseEntity.status(HttpStatus.OK).body(this.engineClient.start().timeout(Duration.ofSeconds(3)));
		} catch (Exception e) {
			this.logger.error("=*=> ERROR: ", e);
			return ResponseEntity.internalServerError().body(Mono.just(false).timeout(Duration.ofSeconds(3)));
		}
	}

	@PostMapping(value = "/stop", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Mono<Boolean>> stop() {
		this.logger.debug("===> TetrisController.stop()");
		try {
			return ResponseEntity.status(HttpStatus.OK).body(this.engineClient.stop().timeout(Duration.ofSeconds(3)));
		} catch (Exception e) {
			this.logger.error("=*=> ERROR: ", e);
			return ResponseEntity.internalServerError().body(Mono.just(false).timeout(Duration.ofSeconds(3)));
		}
	}

	@GetMapping(value = "/getFigureConversation", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public ResponseEntity<Flux<FigureOperation>> getFigureConversation() {
		this.logger.debug("===> TetrisController.getFigureConversation()");
		Flux<FigureOperation> fluxOfFigures = null;
		try {
			fluxOfFigures = this.engineClient.getFigureConversation().doOnNext(figure -> {
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
				return Mono.empty();
			});
			return ResponseEntity.status(HttpStatus.OK)
					.body(fluxOfFigures.delayElements(Duration.ofMillis(100)).timeout(Duration.ofHours(1)));
		} catch (Exception e) {
			this.logger.error("=*=> ERROR: ", e);
			return ResponseEntity.internalServerError().body(Flux.empty());
		}
	}

	@GetMapping(value = "/getBoardConversation", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public ResponseEntity<Flux<BoardOperation>> getBoardConversation() {
		this.logger.debug("===> TetrisController.getBoardConversation()");
		Flux<BoardOperation> fluxOfBoards = null;
		try {
			fluxOfBoards = this.engineClient.getBoardConversation().doOnNext(figure -> {
				this.logger.debug("===> API - Flux.from.boardPublisher - NEXT = " + figure);
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
				return Mono.empty();
			});
			;
			return ResponseEntity.status(HttpStatus.OK)
					.body(fluxOfBoards.delayElements(Duration.ofMillis(50)).timeout(Duration.ofHours(1)));
		} catch (Exception e) {
			this.logger.error("=*=> ERROR: ", e);
			return ResponseEntity.internalServerError().body(Flux.empty());
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
}