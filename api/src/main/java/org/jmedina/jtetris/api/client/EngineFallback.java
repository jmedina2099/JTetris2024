package org.jmedina.jtetris.api.client;

import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestHeader;

import reactor.core.publisher.Mono;

/**
 * @author Jorge Medina
 *
 */
@Component
public class EngineFallback implements EngineClient {

	public Mono<Boolean> isUp(@RequestHeader("authorization") String auth) {
		return Mono.just(false);
	}

	public Mono<Boolean> start(@RequestHeader("authorization") String auth) {
		return Mono.just(false);
	}

	public Mono<Boolean> stop(@RequestHeader("authorization") String auth) {
		return Mono.just(false);
	}

	@Override
	public Mono<Boolean> moveRight(@RequestHeader("authorization") String auth) {
		return Mono.just(false);
	}

	@Override
	public Mono<Boolean> moveLeft(@RequestHeader("authorization") String auth) {
		return Mono.just(false);
	}

	@Override
	public Mono<Boolean> rotateRight(@RequestHeader("authorization") String auth) {
		return Mono.just(false);
	}

	@Override
	public Mono<Boolean> rotateLeft(@RequestHeader("authorization") String auth) {
		return Mono.just(false);
	}

	@Override
	public Mono<Boolean> bottomDown(@RequestHeader("authorization") String auth) {
		return Mono.empty();
	}
}