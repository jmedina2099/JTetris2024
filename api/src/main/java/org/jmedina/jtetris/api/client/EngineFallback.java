package org.jmedina.jtetris.api.client;

import org.jmedina.jtetris.api.model.Box;
import org.jmedina.jtetris.api.model.Message;
import org.springframework.stereotype.Component;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author Jorge Medina
 *
 */
@Component
public class EngineFallback implements EngineClient {

	@Override
	public Mono<Message> start() {
		return Mono.just(new Message("START FALLBACK"));
	}

	@Override
	public Flux<Box> moveRight() {
		return Flux.empty();
	}

	@Override
	public Flux<Box> moveLeft() {
		return Flux.empty();
	}

	@Override
	public Flux<Box> moveDown() {
		return Flux.empty();
	}

	@Override
	public Flux<Box> rotateRight() {
		return Flux.empty();
	}

	@Override
	public Flux<Box> rotateLeft() {
		return Flux.empty();
	}

	@Override
	public Mono<Void> bottomDown() {
		return Mono.empty();
	}
}