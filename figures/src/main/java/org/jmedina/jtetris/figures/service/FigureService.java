package org.jmedina.jtetris.figures.service;

import org.jmedina.jtetris.common.model.FigureOperation;
import org.jmedina.jtetris.figures.exception.ServiceException;
import org.jmedina.jtetris.figures.figure.BoxDB;
import org.jmedina.jtetris.figures.figure.FigureDB;

import reactor.core.publisher.Mono;

/**
 * @author Jorge Medina
 *
 */
public interface FigureService {

	public Mono<FigureOperation<BoxDB,FigureDB<BoxDB>>> askForNextFigureOperation() throws ServiceException;

	public void loadFigurasFromDB();
}