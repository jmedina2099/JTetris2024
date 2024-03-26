package org.jmedina.jtetris.api.client;

import org.jmedina.jtetris.api.model.Box;
import org.jmedina.jtetris.api.model.Message;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;

import reactivefeign.spring.config.ReactiveFeignClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author Jorge Medina
 *
 */
@ReactiveFeignClient(value = "engineClient", url = "${api.engine.base-url}", fallback = EngineFallback.class)
public interface EngineClient {

	@GetMapping(value = "/start", produces = "application/json")
	public Mono<Message> start();

	@GetMapping(value = "/moveRight", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<Box> moveRight();

	@GetMapping(value = "/moveLeft", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<Box> moveLeft();

	@GetMapping(value = "/moveDown", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<Box> moveDown();

	@GetMapping(value = "/rotateRight", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<Box> rotateRight();

	@GetMapping(value = "/rotateLeft", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<Box> rotateLeft();

	@GetMapping(value = "/bottomDown", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<Void> bottomDown();
}