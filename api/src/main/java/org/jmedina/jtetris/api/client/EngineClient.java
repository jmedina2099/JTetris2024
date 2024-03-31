package org.jmedina.jtetris.api.client;

import org.jmedina.jtetris.api.model.Box;
import org.jmedina.jtetris.api.model.Figure;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;

import feign.Headers;
import reactivefeign.spring.config.ReactiveFeignClient;
import reactor.core.publisher.Flux;

/**
 * @author Jorge Medina
 *
 */
@ReactiveFeignClient(value = "engineClient", url = "${api.engine.base-url}", fallback = EngineFallback.class)
@Headers("Connection: keep-alive")
public interface EngineClient {

	@GetMapping(value = "/start", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<Figure> start();

	@GetMapping(value = "/moveRight", produces = MediaType.APPLICATION_JSON_VALUE)
	public Flux<Box> moveRight();

	@GetMapping(value = "/moveLeft", produces = MediaType.APPLICATION_JSON_VALUE)
	public Flux<Box> moveLeft();

	@GetMapping(value = "/rotateRight", produces = MediaType.APPLICATION_JSON_VALUE)
	public Flux<Box> rotateRight();

	@GetMapping(value = "/rotateLeft", produces = MediaType.APPLICATION_JSON_VALUE)
	public Flux<Box> rotateLeft();

	@GetMapping(value = "/bottomDown", produces = MediaType.APPLICATION_JSON_VALUE)
	public Flux<Box> bottomDown();

}