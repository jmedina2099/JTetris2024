package org.jmedina.jtetris.engine.client;

import org.jmedina.jtetris.engine.figure.Figure;
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
	public Flux<Figure> start() {
		return Flux.empty();
	}

	@Override
	public Mono<Void> askForNextFigure() {
		return Mono.empty();
	}
}