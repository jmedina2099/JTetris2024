package org.jmedina.jtetris.api.client;

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
@ReactiveFeignClient(value = "engineClient", url = "${api.engine.base-url}", fallback = EngineFallback.class)
public interface EngineClient {

	@GetMapping(value = "/isUp", produces = MediaType.APPLICATION_JSON_VALUE)
	public Mono<Boolean> isUp(@RequestHeader("authorization") String auth);

	@PostMapping(value = "/start", produces = MediaType.APPLICATION_JSON_VALUE)
	public Mono<Boolean> start(@RequestHeader("authorization") String auth);

	@PostMapping(value = "/stop", produces = MediaType.APPLICATION_JSON_VALUE)
	public Mono<Boolean> stop(@RequestHeader("authorization") String auth);

	@PostMapping(value = "/moveRight", produces = MediaType.APPLICATION_JSON_VALUE)
	public Mono<Boolean> moveRight(@RequestHeader("authorization") String auth);

	@PostMapping(value = "/moveLeft", produces = MediaType.APPLICATION_JSON_VALUE)
	public Mono<Boolean> moveLeft(@RequestHeader("authorization") String auth);

	@PostMapping(value = "/rotateRight", produces = MediaType.APPLICATION_JSON_VALUE)
	public Mono<Boolean> rotateRight(@RequestHeader("authorization") String auth);

	@PostMapping(value = "/rotateLeft", produces = MediaType.APPLICATION_JSON_VALUE)
	public Mono<Boolean> rotateLeft(@RequestHeader("authorization") String auth);

	@PostMapping(value = "/bottomDown", produces = MediaType.APPLICATION_JSON_VALUE)
	public Mono<Boolean> bottomDown(@RequestHeader("authorization") String auth);

}