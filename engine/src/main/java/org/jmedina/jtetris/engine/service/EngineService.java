package org.jmedina.jtetris.engine.service;

import java.util.Optional;

import org.jmedina.jtetris.engine.model.FigureOperation;

/**
 * @author Jorge Medina
 *
 */
public interface EngineService {

	public void start();

	public void addFigureOperation(FigureOperation figureOperation);

	public Optional<Boolean> moveRight();

	public Optional<Boolean> moveLeft();

	public Optional<Boolean> rotateRight();

	public Optional<Boolean> rotateLeft();

	public Optional<Boolean> bottomDown();
}
