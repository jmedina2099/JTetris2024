package org.jmedina.jtetris.engine.service;

import java.util.Optional;

import org.jmedina.jtetris.engine.figure.Box;
import org.jmedina.jtetris.engine.figure.Figure;

/**
 * @author Jorge Medina
 *
 */
public interface EngineService {

	public void start();

	public void addFallingFigure(Figure figure);

	public Optional<Box[]> moveRight();

	public Optional<Box[]> moveLeft();

	public Optional<Box[]> rotateRight();

	public Optional<Box[]> rotateLeft();

	public void bottomDown();
}
