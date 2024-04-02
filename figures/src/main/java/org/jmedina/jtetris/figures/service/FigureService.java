package org.jmedina.jtetris.figures.service;

import org.jmedina.jtetris.figures.exception.ServiceException;
import org.jmedina.jtetris.figures.model.FigureOperation;

/**
 * @author Jorge Medina
 *
 */
public interface FigureService {

	public FigureOperation askForNextFigureOperation() throws ServiceException;

	public void loadFigurasFromDB();
}