package org.jmedina.jtetris.engine.controller;

import java.time.Duration;
import java.util.Optional;

import org.jmedina.jtetris.engine.figure.Box;
import org.jmedina.jtetris.engine.figure.Figure;
import org.jmedina.jtetris.engine.model.Message;
import org.jmedina.jtetris.engine.publisher.EnginePublisher;
import org.jmedina.jtetris.engine.service.EngineService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
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
@CrossOrigin(origins = { "http://localhost:9081", "http://localhost:9083" })
@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class EngineController {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private final EngineService engineService;
	private final EnginePublisher enginePublisher;

	@GetMapping("/hello")
	public Mono<Message> hello() {
		this.logger.debug("===> EngineController.hello()");
		return Mono.just(new Message("Hello from engine reactive!!!"));
	}

	@GetMapping(value = "/start", produces = MediaType.APPLICATION_JSON_VALUE)
	public Flux<Figure> start() {
		this.logger.debug("===> EngineController.start()");
		this.engineService.start();
		return Flux.from(this.enginePublisher).timeout(Duration.ofHours(1)).doOnNext(figure -> {
			this.logger.debug("===> ENGINE - NEXT = " + figure);
			this.engineService.addFallingFigure(figure);
		}).doOnComplete(() -> {
			this.logger.debug("===> ENGINE - COMPLETE!");
		}).doOnCancel(() -> {
			this.logger.debug("===> ENGINE - CANCEL!");
		}).doOnTerminate(() -> {
			this.logger.debug("===> ENGINE - TERMINATE!");
		}).doOnError(e -> {
			this.logger.error("==*=> ERROR =", e);
		});
	}

	@GetMapping(value = "/moveRight", produces = MediaType.APPLICATION_JSON_VALUE)
	public Flux<Box> moveRight() {
		this.logger.debug("===> EngineController.moveRight()");
		Optional<Box[]> optional = this.engineService.moveRight();
		return optional.isPresent() ? Flux.fromArray(optional.get()) : Flux.empty();
	}

	@GetMapping(value = "/moveLeft", produces = MediaType.APPLICATION_JSON_VALUE)
	public Flux<Box> moveLeft() {
		this.logger.debug("===> EngineController.moveLeft()");
		Optional<Box[]> optional = this.engineService.moveLeft();
		return optional.isPresent() ? Flux.fromArray(optional.get()) : Flux.empty();
	}

	@GetMapping(value = "/rotateRight", produces = MediaType.APPLICATION_JSON_VALUE)
	public Flux<Box> rotateRight() {
		this.logger.debug("===> EngineController.rotateRight()");
		Optional<Box[]> optional = this.engineService.rotateRight();
		return optional.isPresent() ? Flux.fromArray(optional.get()) : Flux.empty();
	}

	@GetMapping(value = "/rotateLeft", produces = MediaType.APPLICATION_JSON_VALUE)
	public Flux<Box> rotateLeft() {
		this.logger.debug("===> EngineController.rotateLeft()");
		Optional<Box[]> optional = this.engineService.rotateLeft();
		return optional.isPresent() ? Flux.fromArray(optional.get()) : Flux.empty();
	}

	@GetMapping(value = "/bottomDown", produces = MediaType.APPLICATION_JSON_VALUE)
	public Flux<Box> bottomDown() {
		this.logger.debug("===> EngineController.bottomDown()");
		Optional<Box[]> optional = this.engineService.bottomDown();
		return optional.isPresent() ? Flux.fromArray(optional.get()) : Flux.empty();
	}

}