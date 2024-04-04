package org.jmedina.jtetris.engine.service;

import org.jmedina.jtetris.engine.model.FigureOperation;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author Jorge Medina
 *
 */
public interface FigureService {

	public Flux<FigureOperation> getFigureConversation();

	public Mono<Boolean> askForNextFigure();

	public Mono<Boolean> stop();
}
