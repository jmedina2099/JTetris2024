package org.jmedina.jtetris.api.client;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;

import reactivefeign.spring.config.ReactiveFeignClient;
import reactor.core.publisher.Mono;

/**
 * @author Jorge Medina
 *
 */
@ReactiveFeignClient(value = "engineClient", url = "${api.engine.base-url}", fallback = EngineFallback.class)
public interface EngineClient {

	@PostMapping(value = "/stop", produces = MediaType.APPLICATION_JSON_VALUE)
	public Mono<Boolean> stop();

	@PostMapping(value = "/moveRight", produces = MediaType.APPLICATION_JSON_VALUE)
	public Mono<Boolean> moveRight();

	@PostMapping(value = "/moveLeft", produces = MediaType.APPLICATION_JSON_VALUE)
	public Mono<Boolean> moveLeft();

	@PostMapping(value = "/rotateRight", produces = MediaType.APPLICATION_JSON_VALUE)
	public Mono<Boolean> rotateRight();

	@PostMapping(value = "/rotateLeft", produces = MediaType.APPLICATION_JSON_VALUE)
	public Mono<Boolean> rotateLeft();

	@PostMapping(value = "/bottomDown", produces = MediaType.APPLICATION_JSON_VALUE)
	public Mono<Boolean> bottomDown();

}