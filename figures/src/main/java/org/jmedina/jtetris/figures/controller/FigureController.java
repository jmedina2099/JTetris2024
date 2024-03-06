package org.jmedina.jtetris.figures.controller;

import org.jmedina.jtetris.figures.service.FigureService;
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
public class FigureController {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private final FigureService figureService;

	@GetMapping("/hello")
	public Mono<ResponseEntity<String>> hello() {
		this.logger.debug("===> FigureHandler.hello()");
		return Mono.just(ResponseEntity.status(HttpStatus.OK).body("Hello from figures reactive!!!"));
	}

	@GetMapping("/askForNextFigure")
	public Mono<ResponseEntity<Boolean>> askForNextFigure() {
		this.logger.debug("===> FigureHandler.askForNextFigure()");
		return Mono.just(ResponseEntity.status(HttpStatus.OK).body(this.figureService.askForNextFigure()));
	}

}