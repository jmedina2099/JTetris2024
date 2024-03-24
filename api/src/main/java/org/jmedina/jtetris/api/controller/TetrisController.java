package org.jmedina.jtetris.api.controller;

import org.jmedina.jtetris.api.client.EngineClient;
import org.jmedina.jtetris.api.model.Box;
import org.jmedina.jtetris.api.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author Jorge Medina
 *
 */
@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class TetrisController {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private EngineClient engineClient;

	@GetMapping("/hello")
	public ResponseEntity<Mono<Message>> hello() {
		this.logger.debug("===> TetrisController.hello()");
		return ResponseEntity.status(HttpStatus.OK).body(Mono.just(new Message("Hello from api reactive!!!")));
	}

	@GetMapping("/start")
	public ResponseEntity<Mono<Message>> start() {
		this.logger.debug("===> TetrisController.start()");
		return ResponseEntity.status(HttpStatus.OK).body(this.engineClient.start());
	}

	@GetMapping(value = "/moveRight", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public ResponseEntity<Flux<Box>> moveRight() {
		this.logger.debug("===> TetrisController.moveRight()");
		return ResponseEntity.status(HttpStatus.OK).body(this.engineClient.moveRight());
	}

	@GetMapping(value = "/moveLeft", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public ResponseEntity<Flux<Box>> moveLeft() {
		this.logger.debug("===> TetrisController.moveLeft()");
		return ResponseEntity.status(HttpStatus.OK).body(this.engineClient.moveLeft());
	}

	@GetMapping(value = "/moveDown", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public ResponseEntity<Flux<Box>> moveDown() {
		this.logger.debug("===> TetrisController.moveDown()");
		return ResponseEntity.status(HttpStatus.OK).body(this.engineClient.moveDown());
	}
	
	@GetMapping(value = "/rotateRight", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public ResponseEntity<Flux<Box>> rotateRight() {
		this.logger.debug("===> EngineController.rotateRight()");
		return ResponseEntity.status(HttpStatus.OK).body(this.engineClient.rotateRight());
	}

	@GetMapping(value = "/rotateLeft", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public ResponseEntity<Flux<Box>> rotateLeft() {
		this.logger.debug("===> EngineController.rotateLeft()");
		return ResponseEntity.status(HttpStatus.OK).body(this.engineClient.rotateLeft());
	}

	@GetMapping(value = "/bottomDown", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public ResponseEntity<Flux<Void>> bottomDown() {
		this.logger.debug("===> EngineController.bottomDown()");
		return ResponseEntity.status(HttpStatus.OK).body(this.engineClient.bottomDown());
	}
}