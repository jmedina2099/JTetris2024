package org.jmedina.jtetris.engine.client;

import org.jmedina.jtetris.engine.model.FigureOperation;
import org.springframework.stereotype.Component;

import reactor.core.publisher.Mono;

/**
 * @author Jorge Medina
 *
 */
@Component
public class FiguresFallback implements FiguresClient {

	@Override
	public Mono<FigureOperation> getNextFigure() {
		return Mono.empty();
	}

	public Mono<Boolean> stop() {
		return Mono.just(false);
	}
}