package org.jmedina.jtetris.api.client;

import org.jmedina.jtetris.api.model.Board;
import org.jmedina.jtetris.api.model.Figure;
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
	public Mono<Boolean> start() {
		return Mono.just(false);
	}

	@Override
	public Flux<Figure> getFigureConversation() {
		return Flux.empty();
	}

	@Override
	public Flux<Board> getBoardConversation() {
		return Flux.empty();
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