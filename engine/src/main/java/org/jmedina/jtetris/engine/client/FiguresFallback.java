package org.jmedina.jtetris.engine.client;

import org.jmedina.jtetris.engine.model.FigureOperation;
import org.springframework.stereotype.Component;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author Jorge Medina
 *
 */
@Component
public class FiguresFallback implements FiguresClient {

	@Override
	public Flux<FigureOperation> getFigureConversation() {
		return Flux.empty();
	}

	@Override
	public Mono<Boolean> askForNextFigure() {
		return Mono.empty();
	}

	public Mono<Boolean> stop() {
		return Mono.just(false);
	}
}