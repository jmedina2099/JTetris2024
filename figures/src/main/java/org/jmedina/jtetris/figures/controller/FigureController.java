package org.jmedina.jtetris.figures.controller;

import java.time.Duration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jmedina.jtetris.figures.figure.Figure;
import org.jmedina.jtetris.figures.model.Message;
import org.jmedina.jtetris.figures.publisher.FiguresPublisher;
import org.springframework.http.MediaType;
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
@RestController
@RequestMapping
public class FigureController {

	private final Logger logger = LogManager.getLogger(this.getClass());
	private final FiguresPublisher figurePublisher;

	@GetMapping("/hello")
	public Mono<Message> hello() {
		this.logger.debug("===> FigureController.hello()");
		return Mono.just(new Message("Hello from figures reactive!!!"));
	}

	@GetMapping(value = "/start", produces = MediaType.APPLICATION_JSON_VALUE)
	public Flux<Figure> start() {
		this.logger.debug("===> FigureController.askForNextFigure()");
		return Flux.from(this.figurePublisher).timeout(Duration.ofHours(1)).doOnNext(figure -> {
			this.logger.debug("===> FIGURES - NEXT = " + figure);
		}).doOnCancel(() -> {
			this.logger.debug("===> FIGURES - CANCEL!");
		}).doOnTerminate(() -> {
			this.logger.debug("===> FIGURES - TERMINATE!");
		}).doOnError(e -> {
			this.logger.error("==*=> ERROR =", e);
		});
	}

	@GetMapping(value = "/askForNextFigure", produces = MediaType.APPLICATION_JSON_VALUE)
	public Mono<Void> askAndSendNextFigure() {
		this.logger.debug("===> FigureController.askAndSendNextFigure()");
		this.figurePublisher.askAndSendNextFigure();
		return Mono.empty();
	}
}