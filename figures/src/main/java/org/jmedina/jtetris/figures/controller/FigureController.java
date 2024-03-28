package org.jmedina.jtetris.figures.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jmedina.jtetris.figures.figure.Figure;
import org.jmedina.jtetris.figures.model.Message;
import org.jmedina.jtetris.figures.service.FigureService;
import org.springframework.http.MediaType;
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
public class FigureController {

	private final Logger logger = LogManager.getLogger(this.getClass());
	private final FigureService figureService;

	@GetMapping("/hello")
	public Mono<Message> hello() {
		this.logger.debug("===> FigureController.hello()");
		return Mono.just(new Message("Hello from figures reactive!!!"));
	}

	@GetMapping(value = "/askForNextFigure", produces = MediaType.APPLICATION_JSON_VALUE)
	public Mono<Figure> askForNextFigure() {
		this.logger.debug("===> FigureController.askForNextFigure()");
		return Mono.just(this.figureService.askForNextFigure());
	}

}