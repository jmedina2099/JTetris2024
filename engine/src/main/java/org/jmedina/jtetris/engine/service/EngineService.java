package org.jmedina.jtetris.engine.service;

import java.util.Optional;

import org.jmedina.jtetris.engine.figure.Figure;
import org.jmedina.jtetris.engine.model.Board;

/**
 * @author Jorge Medina
 *
 */
public interface EngineService {

	public void start();

	public void addFallingFigure(Figure figure);

	public Optional<Boolean> moveRight();

	public Optional<Boolean> moveLeft();

	public Optional<Boolean> rotateRight();

	public Optional<Boolean> rotateLeft();

	public Optional<Board> bottomDown();
}
