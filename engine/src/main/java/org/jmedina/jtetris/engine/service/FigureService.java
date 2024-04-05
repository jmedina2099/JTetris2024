package org.jmedina.jtetris.engine.service;

import org.jmedina.jtetris.engine.model.FigureOperation;

import reactor.core.publisher.Mono;

/**
 * @author Jorge Medina
 *
 */
public interface FigureService {

	public Mono<FigureOperation> getNextFigure();

	public Mono<Boolean> stop();
}
