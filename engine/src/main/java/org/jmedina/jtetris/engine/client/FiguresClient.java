package org.jmedina.jtetris.engine.client;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import reactivefeign.spring.config.ReactiveFeignClient;
import reactor.core.publisher.Mono;

/**
 * @author Jorge Medina
 *
 */
@ReactiveFeignClient(value = "figuresClient", url = "${engine.figures.base-url}", fallback = FiguresFallback.class)
public interface FiguresClient {

	@GetMapping(value = "/isUp", produces = MediaType.APPLICATION_JSON_VALUE)
	public Mono<Boolean> isUp(@RequestHeader("authorization") String auth);

	@PostMapping(value = "/start", produces = MediaType.APPLICATION_JSON_VALUE)
	public Mono<Boolean> start(@RequestHeader("authorization") String auth);

	@PostMapping(value = "/stop", produces = MediaType.APPLICATION_JSON_VALUE)
	public Mono<Boolean> stop(@RequestHeader("authorization") String auth);

}