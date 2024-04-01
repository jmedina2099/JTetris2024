package org.jmedina.jtetris.engine.controller;

import java.time.Duration;
import java.util.Optional;

import org.jmedina.jtetris.engine.figure.Figure;
import org.jmedina.jtetris.engine.model.Board;
import org.jmedina.jtetris.engine.model.Message;
import org.jmedina.jtetris.engine.publisher.EnginePublisher;
import org.jmedina.jtetris.engine.publisher.FigurePublisher;
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
	private final FigurePublisher figurePublisher;
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
		return Flux.from(this.figurePublisher).timeout(Duration.ofHours(1)).doOnNext(figure -> {
			this.logger.debug("===> ENGINE - start() - NEXT = " + figure);
			this.engineService.addFallingFigure(figure);
		}).doOnComplete(() -> {
			this.logger.debug("===> ENGINE - figurePublisher - COMPLETE!");
		}).doOnCancel(() -> {
			this.logger.debug("===> ENGINE - figurePublisher - CANCEL!");
		}).doOnTerminate(() -> {
			this.logger.debug("===> ENGINE - figurePublisher - TERMINATE!");
		}).doOnError(e -> {
			this.logger.error("==*=> ERROR =", e);
		}).mergeWith(this.enginePublisher).timeout(Duration.ofHours(1)).doOnNext(figure -> {
			this.logger.debug("===> ENGINE - enginePublisher - NEXT = " + figure);
		}).doOnComplete(() -> {
			this.logger.debug("===> ENGINE - enginePublisher - COMPLETE!");
		}).doOnCancel(() -> {
			this.logger.debug("===> ENGINE - enginePublisher - CANCEL!");
		}).doOnTerminate(() -> {
			this.logger.debug("===> ENGINE - enginePublisher - TERMINATE!");
		}).doOnError(e -> {
			this.logger.error("==*=> ERROR =", e);
		});
	}

	@GetMapping(value = "/moveRight", produces = MediaType.APPLICATION_JSON_VALUE)
	public Mono<Boolean> moveRight() {
		this.logger.debug("===> EngineController.moveRight()");
		Optional<Boolean> optional = this.engineService.moveRight();
		return optional.isPresent() ? Mono.just(optional.get()) : Mono.just(false);
	}

	@GetMapping(value = "/moveLeft", produces = MediaType.APPLICATION_JSON_VALUE)
	public Mono<Boolean> moveLeft() {
		this.logger.debug("===> EngineController.moveLeft()");
		Optional<Boolean> optional = this.engineService.moveLeft();
		return optional.isPresent() ? Mono.just(optional.get()) : Mono.just(false);
	}

	@GetMapping(value = "/rotateRight", produces = MediaType.APPLICATION_JSON_VALUE)
	public Mono<Boolean> rotateRight() {
		this.logger.debug("===> EngineController.rotateRight()");
		Optional<Boolean> optional = this.engineService.rotateRight();
		return optional.isPresent() ? Mono.just(optional.get()) : Mono.just(false);
	}

	@GetMapping(value = "/rotateLeft", produces = MediaType.APPLICATION_JSON_VALUE)
	public Mono<Boolean> rotateLeft() {
		this.logger.debug("===> EngineController.rotateLeft()");
		Optional<Boolean> optional = this.engineService.rotateLeft();
		return optional.isPresent() ? Mono.just(optional.get()) : Mono.just(false);
	}

	@GetMapping(value = "/bottomDown", produces = MediaType.APPLICATION_JSON_VALUE)
	public Mono<Board> bottomDown() {
		this.logger.debug("===> EngineController.bottomDown()");
		Optional<Board> optional = this.engineService.bottomDown();
		return optional.isPresent() ? Mono.just(optional.get()) : Mono.empty();
	}

}