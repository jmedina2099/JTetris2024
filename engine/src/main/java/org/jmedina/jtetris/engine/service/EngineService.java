package org.jmedina.jtetris.engine.service;

import java.util.Optional;

import org.jmedina.jtetris.engine.model.FigureOperation;
import org.jmedina.jtetris.engine.publisher.EnginePublisher;
import org.jmedina.jtetris.engine.publisher.NextFigurePublisher;

/**
 * @author Jorge Medina
 *
 */
public interface EngineService {

	public void start(NextFigurePublisher nextFigurePublisher, EnginePublisher enginePublisher);

	public void stop();

	public void addFigureOperation(FigureOperation figureOperation);

	public Optional<Boolean> moveRight();

	public Optional<Boolean> moveLeft();

	public Optional<Boolean> rotateRight();

	public Optional<Boolean> rotateLeft();

	public Optional<Boolean> bottomDown();
}