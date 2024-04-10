package org.jmedina.jtetris.figures.service;

import org.jmedina.jtetris.figures.exception.ServiceException;
import org.jmedina.jtetris.figures.model.FigureOperation;

import reactor.core.publisher.Mono;

/**
 * @author Jorge Medina
 *
 */
public interface FigureService {

	public Mono<FigureOperation> askForNextFigureOperation() throws ServiceException;

	public void loadFigurasFromDB();
}