package org.jmedina.jtetris.engine.client;

import org.springframework.stereotype.Component;

import reactor.core.publisher.Mono;

/**
 * @author Jorge Medina
 *
 */
@Component
public class FiguresFallback implements FiguresClient {

	@Override
	public Mono<Boolean> askForNextFigureOperation() {
		return Mono.<Boolean>empty();
	}

	public Mono<Boolean> stop() {
		return Mono.just(false);
	}
}