package org.jmedina.jtetris.figures.service;

import org.jmedina.jtetris.figures.exception.ServiceException;
import org.jmedina.jtetris.figures.figure.Figure;

/**
 * @author Jorge Medina
 *
 */
public interface FigureService {

	public Figure askForNextFigure() throws ServiceException;

	public void loadFigurasFromDB();
}