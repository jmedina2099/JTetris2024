package org.jmedina.jtetris.api.client;

import org.springframework.stereotype.Component;

import reactor.core.publisher.Mono;

/**
 * @author Jorge Medina
 *
 */
@Component
public class EngineFallback implements EngineClient {

	public Mono<Boolean> stop() {
		return Mono.just(false);
	}

	@Override
	public Mono<Boolean> moveRight() {
		return Mono.just(false);
	}

	@Override
	public Mono<Boolean> moveLeft() {
		return Mono.just(false);
	}

	@Override
	public Mono<Boolean> rotateRight() {
		return Mono.just(false);
	}

	@Override
	public Mono<Boolean> rotateLeft() {
		return Mono.just(false);
	}

	@Override
	public Mono<Boolean> bottomDown() {
		return Mono.empty();
	}
}