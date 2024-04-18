package org.jmedina.jtetris.engine.service;

import java.util.stream.Stream;

import org.jmedina.jtetris.engine.figure.BoxMotion;
import org.jmedina.jtetris.engine.figure.FigureMotion;

/**
 * @author Jorge Medina
 *
 */
public interface GridSupportService {

	public void initializeGrid();

	public void addToGrid(FigureMotion<BoxMotion> figure);

	public void removeFromGrid(FigureMotion<BoxMotion> figure);

	public Stream<Boolean> getStreamRow(int indexY);

	public void removeGridRow(int indexY);

	public boolean noHit(FigureMotion<BoxMotion> figure, int offsetX, int offsetY);

	public void moveDownGrid(int indexY);

}
