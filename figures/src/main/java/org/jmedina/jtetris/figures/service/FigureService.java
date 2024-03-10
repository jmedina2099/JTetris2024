package org.jmedina.jtetris.figures.service;

import org.jmedina.jtetris.figures.exception.ServiceException;

/**
 * @author Jorge Medina
 *
 */
public interface FigureService {

	public void askForNextFigure() throws ServiceException;

	public void loadFigurasFromDB();
}