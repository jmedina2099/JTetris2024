package org.jmedina.jtetris.engine.controller;

import org.jmedina.jtetris.engine.service.FigureService;
import org.jmedina.jtetris.engine.tool.Engine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
public class EngineController {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private final FigureService figureService;
	private final Engine engine;

	@GetMapping("/hello")
	public Mono<ResponseEntity<String>> hello() {
		this.logger.debug("===> EngineController.hello()");
		return Mono.just(ResponseEntity.status(HttpStatus.OK).body("Hello from engine reactive!!!"));
	}

	@GetMapping(value = "/start", produces = MediaType.APPLICATION_JSON_VALUE)
	public Mono<ResponseEntity<Boolean>> start() {
		this.logger.debug("===> EngineController.start()");
		this.figureService.askForNextFigure();
		return Mono.just(ResponseEntity.status(HttpStatus.OK).body(true));
	}

	@GetMapping(value = "/moveRight", produces = MediaType.APPLICATION_JSON_VALUE)
	public Mono<ResponseEntity<Boolean>> moveRight() {
		this.logger.debug("===> EngineController.moveRight()");
		this.engine.moveRight();
		return Mono.just(ResponseEntity.status(HttpStatus.OK).body(true));
	}

	@GetMapping(value = "/moveLeft", produces = MediaType.APPLICATION_JSON_VALUE)
	public Mono<ResponseEntity<Boolean>> moveLeft() {
		this.logger.debug("===> EngineController.moveLeft()");
		this.engine.moveLeft();
		return Mono.just(ResponseEntity.status(HttpStatus.OK).body(true));
	}

	@GetMapping(value = "/moveDown", produces = MediaType.APPLICATION_JSON_VALUE)
	public Mono<ResponseEntity<Boolean>> moveDown() {
		this.logger.debug("===> EngineController.moveDown()");
		this.engine.moveDown();
		return Mono.just(ResponseEntity.status(HttpStatus.OK).body(true));
	}
}