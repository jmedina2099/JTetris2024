package org.jmedina.jtetris.figures.service;

import org.jmedina.jtetris.figures.domain.Figura;

import reactor.core.publisher.Flux;

/**
 * @author Jorge Medina
 *
 */
public interface FigureTemplateOperations {

	public Flux<Figura> findAll();	
}