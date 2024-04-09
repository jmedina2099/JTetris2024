package org.jmedina.jtetris.engine.client;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import reactivefeign.spring.config.ReactiveFeignClient;
import reactor.core.publisher.Mono;

/**
 * @author Jorge Medina
 *
 */
@ReactiveFeignClient(value = "figuresClient", url = "${engine.figures.base-url}", fallback = FiguresFallback.class)
public interface FiguresClient {

	@GetMapping(value = "/askForNextFigureOperation", produces = MediaType.APPLICATION_JSON_VALUE)
	public Mono<Boolean> askForNextFigureOperation();

	@PostMapping(value = "/stop", produces = MediaType.APPLICATION_JSON_VALUE)
	public Mono<Boolean> stop();
}