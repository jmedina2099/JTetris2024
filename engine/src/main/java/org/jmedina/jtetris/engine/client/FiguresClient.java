package org.jmedina.jtetris.engine.client;

import org.jmedina.jtetris.engine.model.Message;
import org.springframework.web.bind.annotation.GetMapping;

import reactivefeign.spring.config.ReactiveFeignClient;
import reactor.core.publisher.Mono;

/**
 * @author Jorge Medina
 *
 */
@ReactiveFeignClient(value = "figuresClient", url = "${engine.figures.base-url}", fallback = FiguresFallback.class)
public interface FiguresClient {

	@GetMapping(value = "/askForNextFigure", produces = "application/json")
	public Mono<Message> askForNextFigure();
}