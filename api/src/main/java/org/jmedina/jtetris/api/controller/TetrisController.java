package org.jmedina.jtetris.api.controller;

import org.jmedina.jtetris.api.client.EngineClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

/**
 * @author Jorge Medina
 *
 */
@RequiredArgsConstructor
@RestController
@RequestMapping
public class TetrisController {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private final EngineClient engineClient;

	@GetMapping("/hello")
	public Mono<ResponseEntity<String>> hello() {
		this.logger.debug("===> TetrisController.hello()");
		return Mono.just(ResponseEntity.status(HttpStatus.OK).body("Hello from api reactive!!!"));
	}

	@GetMapping("/start")
	public Mono<ResponseEntity<Boolean>> start() {
		this.logger.debug("===> TetrisController.start()");
		return Mono.just(ResponseEntity.status(HttpStatus.OK).body(this.engineClient.start()));
	}

	@GetMapping("/moveRight")
	public Mono<ResponseEntity<Boolean>> moveRight() {
		this.logger.debug("===> TetrisController.moveRight()");
		return Mono.just(ResponseEntity.status(HttpStatus.OK).body(this.engineClient.moveRight()));
	}

	@GetMapping("/moveLeft")
	public Mono<ResponseEntity<Boolean>> moveLeft() {
		this.logger.debug("===> TetrisController.moveLeft()");
		return Mono.just(ResponseEntity.status(HttpStatus.OK).body(this.engineClient.moveLeft()));
	}

	@GetMapping("/moveDown")
	public Mono<ResponseEntity<Boolean>> moveDown() {
		this.logger.debug("===> TetrisController.moveDown()");
		return Mono.just(ResponseEntity.status(HttpStatus.OK).body(this.engineClient.moveDown()));
	}
}