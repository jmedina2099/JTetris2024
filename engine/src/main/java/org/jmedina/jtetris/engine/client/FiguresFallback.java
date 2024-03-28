package org.jmedina.jtetris.engine.client;

import org.jmedina.jtetris.engine.figure.Figure;
import org.springframework.stereotype.Component;

import reactor.core.publisher.Mono;

/**
 * @author Jorge Medina
 *
 */
@Component
public class FiguresFallback implements FiguresClient {

	@Override
	public Mono<Figure> askForNextFigure() {
		return Mono.empty();
	}
}