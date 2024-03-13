package org.jmedina.jtetris.engine.controller;

import org.jmedina.jtetris.engine.model.Message;
import org.jmedina.jtetris.engine.service.FigureService;
import org.jmedina.jtetris.engine.tool.Engine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
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
@CrossOrigin(origins = { "http://localhost:9081", "http://localhost:9083" })
@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class EngineController {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private final FigureService figureService;
	private final Engine engine;

	@GetMapping("/hello")
	public Mono<Message> hello() {
		this.logger.debug("===> EngineController.hello()");
		return Mono.just(new Message("Hello from engine reactive!!!"));
	}

	@GetMapping(value = "/start")
	public Mono<Message> start() {
		this.logger.debug("===> EngineController.start()");
		return this.figureService.askForNextFigure();
	}

	@GetMapping(value = "/moveRight")
	public Mono<Message> moveRight() {
		this.logger.debug("===> EngineController.moveRight()");
		this.engine.moveRight();
		return Mono.just(new Message("OK from engine!!!"));
	}

	@GetMapping(value = "/moveLeft")
	public Mono<Message> moveLeft() {
		this.logger.debug("===> EngineController.moveLeft()");
		this.engine.moveLeft();
		return Mono.just(new Message("OK from engine!!!"));
	}

	@GetMapping(value = "/moveDown")
	public Mono<Message> moveDown() {
		this.logger.debug("===> EngineController.moveDown()");
		this.engine.moveDown();
		return Mono.just(new Message("OK from engine!!!"));
	}

	@GetMapping(value = "/rotateRight")
	public Mono<Message> rotateRight() {
		this.logger.debug("===> EngineController.rotateRight()");
		this.engine.rotateRight();
		return Mono.just(new Message("OK from engine!!!"));
	}

	@GetMapping(value = "/rotateLeft")
	public Mono<Message> rotateLeft() {
		this.logger.debug("===> EngineController.rotateLeft()");
		this.engine.rotateLeft();
		return Mono.just(new Message("OK from engine!!!"));
	}

}