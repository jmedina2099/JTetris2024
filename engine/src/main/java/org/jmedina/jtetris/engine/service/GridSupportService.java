package org.jmedina.jtetris.engine.service;

import java.util.stream.Stream;

import org.jmedina.jtetris.engine.figure.Figure;

/**
 * @author Jorge Medina
 *
 */
public interface GridSupportService {

	public void initializeGrid();

	public void addToGrid(Figure figure);

	public void removeFromGrid(Figure figure);

	public Stream<Boolean> getStreamRow(int indexY);

	public void removeGridRow(int indexY);

	public boolean noHit(Figure figure, int offsetX, int offsetY);

	public void moveDownGrid(int indexY);

}
