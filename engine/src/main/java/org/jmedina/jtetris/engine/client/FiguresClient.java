package org.jmedina.jtetris.engine.client;

import org.jmedina.jtetris.engine.figure.Figure;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;

import feign.Headers;
import reactivefeign.spring.config.ReactiveFeignClient;
import reactor.core.publisher.Mono;

/**
 * @author Jorge Medina
 *
 */
@ReactiveFeignClient(value = "figuresClient", url = "${engine.figures.base-url}", fallback = FiguresFallback.class)
@Headers("Connection: keep-alive")
public interface FiguresClient {

	@GetMapping(value = "/askForNextFigure", produces = MediaType.APPLICATION_JSON_VALUE)
	public Mono<Figure> askForNextFigure();
}