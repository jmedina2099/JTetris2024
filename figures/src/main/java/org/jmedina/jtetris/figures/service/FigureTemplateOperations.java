package org.jmedina.jtetris.figures.service;

import org.jmedina.jtetris.figures.domain.Figura;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author Jorge Medina
 *
 */
public interface FigureTemplateOperations {

	public Flux<Figura> findAll();

	public Mono<Figura> findByName(String name);
}