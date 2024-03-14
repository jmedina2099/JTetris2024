package org.jmedina.jtetris.engine.tool;

import java.util.ArrayList;
import java.util.List;

import org.jmedina.jtetris.engine.figure.Box;
import org.jmedina.jtetris.engine.figure.Figure;
import org.jmedina.jtetris.engine.service.FigureService;
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
	private final FigureService figureService;
	private final KafkaService kafkaService;
	private final SerializeUtil serializeUtil;
	private Figure fallingFigure;
	private List<Box> falledBoxes;

	public void start() {
		this.falledBoxes = new ArrayList<>();
	}

	public void addFallingFigure(Figure figure) {
		this.logger.debug("==> Engine.addFallingFigure()");
		this.fallingFigure = figure;
	}

	public void moveRight() {
		this.logger.debug("==> Engine.moveRight()");
		double max = this.fallingFigure.getBoxes().stream().mapToDouble(b -> b.getX()).max().getAsDouble();
		double size = Box.SIZE;
		if ((max + 2 * size) <= size * 10) {
			this.fallingFigure.moveRight();
			this.serializeUtil.convertFigureToString(this.fallingFigure)
					.ifPresent(this.kafkaService::sendMessageFigure);
		}
	}

	public void moveLeft() {
		this.logger.debug("==> Engine.moveLeft()");
		double min = this.fallingFigure.getBoxes().stream().mapToDouble(b -> b.getX()).min().getAsDouble();
		double size = Box.SIZE;
		if ((min - size) >= 0) {
			this.fallingFigure.moveLeft();
			this.serializeUtil.convertFigureToString(this.fallingFigure)
					.ifPresent(this.kafkaService::sendMessageFigure);
		}
	}

	public void moveDown() {
		this.logger.debug("==> Engine.moveDown()");
		double yMax = this.fallingFigure.getBoxes().stream().mapToDouble(b -> b.getY()).max().getAsDouble();
		double size = Box.SIZE;
		if ((yMax + size) < size * 20 && canMoveDown(this.fallingFigure.getBoxes())) {
			this.fallingFigure.moveDown();
			this.serializeUtil.convertFigureToString(this.fallingFigure)
					.ifPresent(this.kafkaService::sendMessageFigure);
		}
	}

	private boolean canMoveDown(List<Box> listBoxes) {
		return listBoxes.stream().allMatch(
				b -> this.falledBoxes.stream().allMatch(f -> f.getX() != b.getX() || f.getY() > b.getY() + Box.SIZE) );
	}

	public void rotateRight() {
		this.logger.debug("==> Engine.rotateRight()");

		if (this.fallingFigure.numRotations == 0) {
			return;
		}

		int direction = this.fallingFigure.numRotations == 2 ? -1 : 1;
		if (rotate(direction)) {
			this.serializeUtil.convertFigureToString(this.fallingFigure)
					.ifPresent(this.kafkaService::sendMessageFigure);
		}
	}

	public void rotateLeft() {
		this.logger.debug("==> Engine.rotateLeft()");

		if (this.fallingFigure.numRotations == 0) {
			return;
		}

		int direction = -1;
		if (rotate(direction)) {
			this.serializeUtil.convertFigureToString(this.fallingFigure)
					.ifPresent(this.kafkaService::sendMessageFigure);
		}
	}

	public void bottomDown() {
		this.logger.debug("==> Engine.bottomDown()");
		double yMax = this.fallingFigure.getBoxes().stream().mapToDouble(b -> b.getY()).max().getAsDouble();
		double size = Box.SIZE;
		while ((yMax + size) < size * 20 && canMoveDown(this.fallingFigure.getBoxes())) {
			this.fallingFigure.moveDown();
			yMax = this.fallingFigure.getBoxes().stream().mapToDouble(b -> b.getY()).max().getAsDouble();
		}
		this.serializeUtil.convertFigureToString(this.fallingFigure).ifPresent(this.kafkaService::sendMessageFigure);
		this.falledBoxes.addAll(this.fallingFigure.getBoxes());
		this.serializeUtil.convertListOfBoxesToString(this.falledBoxes).ifPresent(this.kafkaService::sendMessageBoard);
		this.figureService.askForNextFigure().subscribe(m -> {
			this.logger.debug("==> askForNextFigure.subscribe = " + m.getContent());
		});
	}

	private boolean rotate(int direction) {
		this.logger.debug("==> Engine.rotate()");

		if (this.fallingFigure.numRotations == 2) {
			if (this.fallingFigure.rotation == 0) {
				this.fallingFigure.rotation++;
			} else {
				direction *= -1;
				this.fallingFigure.rotation = 0;
			}
		} else {
			this.fallingFigure.rotation++;
			this.fallingFigure.rotation = this.fallingFigure.rotation % this.fallingFigure.numRotations;
		}

		this.logger.debug("==> center = {}", this.fallingFigure.getCenter());

		Figure tmpFigure = RotationUtil.rotateFigure(this.fallingFigure, direction);
		double max = tmpFigure.getBoxes().stream().mapToDouble(b -> b.getX()).max().getAsDouble();
		double size = Box.SIZE;
		while ((max + size) > size * 10) {
			tmpFigure.moveLeft();
			max = tmpFigure.getBoxes().stream().mapToDouble(b -> b.getX()).max().getAsDouble();
		}
		double min = tmpFigure.getBoxes().stream().mapToDouble(b -> b.getX()).min().getAsDouble();
		while (min < 0) {
			tmpFigure.moveRight();
			min = tmpFigure.getBoxes().stream().mapToDouble(b -> b.getX()).min().getAsDouble();
		}

		this.fallingFigure.setBoxes(tmpFigure.getBoxes());
		this.fallingFigure.setCenter(tmpFigure.getCenter());
		return true;
	}

}