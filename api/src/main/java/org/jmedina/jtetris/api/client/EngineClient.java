package org.jmedina.jtetris.api.client;

import org.jmedina.jtetris.api.model.Message;
import org.springframework.web.bind.annotation.GetMapping;

import reactivefeign.spring.config.ReactiveFeignClient;
import reactor.core.publisher.Mono;

/**
 * @author Jorge Medina
 *
 */
@ReactiveFeignClient(value = "engineClient", url = "${api.engine.base-url}", fallback = EngineFallback.class)
public interface EngineClient {

	@GetMapping(value = "/start", produces = "application/json")
	public Mono<Message> start();

	@GetMapping(value = "/moveRight", produces = "application/json")
	public Mono<Message> moveRight();

	@GetMapping(value = "/moveLeft", produces = "application/json")
	public Mono<Message> moveLeft();

	@GetMapping(value = "/moveDown", produces = "application/json")
	public Mono<Message> moveDown();

	@GetMapping(value = "/rotateRight", produces = "application/json")
	public Mono<Message> rotateRight();

	@GetMapping(value = "/rotateLeft", produces = "application/json")
	public Mono<Message> rotateLeft();

	@GetMapping(value = "/bottomDown", produces = "application/json")
	public Mono<Message> bottomDown();
}