package org.jmedina.jtetris.engine.service;

import org.jmedina.jtetris.engine.figure.Figure;

import reactor.core.publisher.Mono;

/**
 * @author Jorge Medina
 *
 */
public interface FigureService {

	public Mono<Figure> askForNextFigure();
}
