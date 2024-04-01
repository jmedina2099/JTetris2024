package org.jmedina.jtetris.api.controller;

import java.time.Duration;

import org.jmedina.jtetris.api.client.EngineClient;
import org.jmedina.jtetris.api.model.Board;
import org.jmedina.jtetris.api.model.Figure;
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
		return ResponseEntity.status(HttpStatus.OK).body(Mono.just(new Message("Hello from api reactive!!!")));
	}

	@GetMapping(value = "/start", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public ResponseEntity<Flux<Figure>> start() {
		this.logger.debug("===> TetrisController.start()");
		return ResponseEntity.status(HttpStatus.OK)
				.body(this.engineClient.start().timeout(Duration.ofHours(1)).doOnNext(figure -> {
					this.logger.debug("===> API - NEXT = " + figure);
				}).doOnComplete(() -> {
					this.logger.debug("===> API - COMPLETE!");
				}).doOnCancel(() -> {
					this.logger.debug("===> API - CANCEL!");
				}).doOnTerminate(() -> {
					this.logger.debug("===> API - TERMINATE!");
				}).doOnError(e -> {
					this.logger.error("==*=> ERROR =", e);
				}));
	}

	@PostMapping(value = "/moveRight", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Mono<Boolean>> moveRight() {
		this.logger.debug("===> TetrisController.moveRight()");
		return ResponseEntity.status(HttpStatus.OK).body(this.engineClient.moveRight());
	}

	@PostMapping(value = "/moveLeft", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Mono<Boolean>> moveLeft() {
		this.logger.debug("===> TetrisController.moveLeft()");
		return ResponseEntity.status(HttpStatus.OK).body(this.engineClient.moveLeft());
	}

	@PostMapping(value = "/rotateRight", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Mono<Boolean>> rotateRight() {
		this.logger.debug("===> EngineController.rotateRight()");
		return ResponseEntity.status(HttpStatus.OK).body(this.engineClient.rotateRight());
	}

	@PostMapping(value = "/rotateLeft", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Mono<Boolean>> rotateLeft() {
		this.logger.debug("===> EngineController.rotateLeft()");
		return ResponseEntity.status(HttpStatus.OK).body(this.engineClient.rotateLeft());
	}

	@PostMapping(value = "/bottomDown", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Mono<Board>> bottomDown() {
		this.logger.debug("===> EngineController.bottomDown()");
		return ResponseEntity.status(HttpStatus.OK).body(this.engineClient.bottomDown());
	}
}