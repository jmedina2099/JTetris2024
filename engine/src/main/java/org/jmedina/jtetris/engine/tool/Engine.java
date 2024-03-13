package org.jmedina.jtetris.engine.tool;

import java.awt.geom.Point2D;

import org.jmedina.jtetris.engine.figure.Box;
import org.jmedina.jtetris.engine.figure.Figure;
import org.jmedina.jtetris.engine.service.KafkaService;
import org.jmedina.jtetris.engine.util.RotationUtil;
import org.jmedina.jtetris.engine.util.SerializeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

/**
 * @author Jorge Medina
 *
 */
@RequiredArgsConstructor
@Component
public class Engine {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private final KafkaService kafkaService;
	private final SerializeUtil serializeUtil;
	private Figure fallingFigure;

	public void addFallingFigure(Figure figure) {
		this.logger.debug("==> Engine.addFallingFigure()");
		this.fallingFigure = figure;
	}

	public boolean moveRight() {
		this.logger.debug("==> Engine.moveRight()");
		double max = this.fallingFigure.getBoxes().stream().mapToDouble(b -> b.getX()).max().getAsDouble();
		double size = Box.SIZE;
		if ((max + 2 * size) <= size * 10) {
			this.fallingFigure.moveRight();
			this.serializeUtil.convertFigureToString(this.fallingFigure).ifPresent(this.kafkaService::sendMessage);
			return true;
		}
		return false;
	}

	public boolean moveLeft() {
		this.logger.debug("==> Engine.moveLeft()");
		double min = this.fallingFigure.getBoxes().stream().mapToDouble(b -> b.getX()).min().getAsDouble();
		double size = Box.SIZE;
		if ((min - size) >= 0) {
			this.fallingFigure.moveLeft();
			this.serializeUtil.convertFigureToString(this.fallingFigure).ifPresent(this.kafkaService::sendMessage);
			return true;
		}
		return false;
	}

	public boolean moveDown() {
		this.logger.debug("==> Engine.moveDown()");
		double max = this.fallingFigure.getBoxes().stream().mapToDouble(b -> b.getY()).max().getAsDouble();
		double size = Box.SIZE;
		if ((max + 2 * size) <= size * 20) {
			this.fallingFigure.moveDown();
			this.serializeUtil.convertFigureToString(this.fallingFigure).ifPresent(this.kafkaService::sendMessage);
			return true;
		}
		return false;
	}

	public void rotateRight() {
		this.logger.debug("==> Engine.rotateRight()");

		int direction = 1;
		if( rotate(this.fallingFigure.center, direction) ) {
			this.serializeUtil.convertFigureToString(this.fallingFigure).ifPresent(this.kafkaService::sendMessage);
		}
	}

	public void rotateLeft() {
		this.logger.debug("==> Engine.rotateLeft()");

		int direction = -1;
		if( rotate(this.fallingFigure.center, direction) ) {
			this.serializeUtil.convertFigureToString(this.fallingFigure).ifPresent(this.kafkaService::sendMessage);
		}
	}

	private boolean rotate(Point2D.Double center, int direction) {
		this.logger.debug("==> Engine.rotate()");
		this.fallingFigure.rotation++;
		this.fallingFigure.rotation = this.fallingFigure.rotation % this.fallingFigure.numRotations;
		
		this.logger.debug("==> center = {}",center);
		
		Figure tmpFigure = RotationUtil.rotateFigure(this.fallingFigure, center, direction);
		this.fallingFigure.setBoxes(tmpFigure.getBoxes());
		return true;
	}
}