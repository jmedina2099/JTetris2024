package org.jmedina.jtetris.engine.client;

import org.jmedina.jtetris.engine.model.FigureOperation;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;

import reactivefeign.spring.config.ReactiveFeignClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author Jorge Medina
 *
 */
@ReactiveFeignClient(value = "figuresClient", url = "${engine.figures.base-url}", fallback = FiguresFallback.class)
public interface FiguresClient {

	@GetMapping(value = "/getFigureConversation", produces = MediaType.APPLICATION_JSON_VALUE)
	public Flux<FigureOperation> getFigureConversation();

	@GetMapping(value = "/askForNextFigure", produces = MediaType.APPLICATION_JSON_VALUE)
	public Mono<Void> askForNextFigure();
}