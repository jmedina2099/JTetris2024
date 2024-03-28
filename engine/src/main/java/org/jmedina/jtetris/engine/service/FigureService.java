package org.jmedina.jtetris.engine.service;

import org.jmedina.jtetris.engine.model.Message;

import reactor.core.publisher.Mono;

/**
 * @author Jorge Medina
 *
 */
public interface FigureService {

	public Mono<Message> askForNextFigure();
}
