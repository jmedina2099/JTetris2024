package org.jmedina.jtetris.engine.service;

import reactor.core.publisher.Mono;

/**
 * @author Jorge Medina
 *
 */
public interface FigureService {

	public Mono<Boolean> stop();
}
