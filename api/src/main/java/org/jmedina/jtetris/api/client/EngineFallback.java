package org.jmedina.jtetris.api.client;

import org.jmedina.jtetris.api.model.Message;
import org.springframework.stereotype.Component;

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
	public Mono<Message> moveRight() {
		return Mono.just(new Message("MOVE RIGHT FALLBACK"));
	}

	@Override
	public Mono<Message> moveLeft() {
		return Mono.just(new Message("MOVE LEFT FALLBACK"));
	}

	@Override
	public Mono<Message> moveDown() {
		return Mono.just(new Message("MOVE DOWN FALLBACK"));
	}

	@Override
	public Mono<Message> rotateRight() {
		return Mono.just(new Message("ROTATE RIGHT FALLBACK"));
	}

	@Override
	public Mono<Message> rotateLeft() {
		return Mono.just(new Message("ROTATE LEFT FALLBACK"));
	}

	@Override
	public Mono<Message> bottomDown() {
		return Mono.just(new Message("BOTTOM DOWN FALLBACK"));
	}
}