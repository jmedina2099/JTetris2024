package org.jmedina.jtetris.api.client;

import org.jmedina.jtetris.api.model.Box;
import org.jmedina.jtetris.api.model.Figure;
import org.springframework.stereotype.Component;

import reactor.core.publisher.Flux;

/**
 * @author Jorge Medina
 *
 */
@Component
public class EngineFallback implements EngineClient {

	@Override
	public Flux<Figure> start() {
		return Flux.empty();
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
	public Flux<Box> rotateRight() {
		return Flux.empty();
	}

	@Override
	public Flux<Box> rotateLeft() {
		return Flux.empty();
	}

	@Override
	public Flux<Box> bottomDown() {
		return Flux.empty();
	}
}