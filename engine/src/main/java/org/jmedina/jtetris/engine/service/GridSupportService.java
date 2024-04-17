package org.jmedina.jtetris.engine.service;

import java.util.stream.Stream;

import org.jmedina.jtetris.engine.figure.FigureForEngine;

/**
 * @author Jorge Medina
 *
 */
public interface GridSupportService {

	public void initializeGrid();

	public void addToGrid(FigureForEngine figure);

	public void removeFromGrid(FigureForEngine figure);

	public Stream<Boolean> getStreamRow(int indexY);

	public void removeGridRow(int indexY);

	public boolean noHit(FigureForEngine figure, int offsetX, int offsetY);

	public void moveDownGrid(int indexY);

}
