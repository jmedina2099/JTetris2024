package org.jmedina.jtetris.engine.tool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

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

	private static final int WIDTH = 10;
	private static final int HEIGHT = 20;
	private Map<Integer, int[]> gridBoxes = new HashMap<>();

	public void start() {
		this.logger.debug("==> Engine.start()");
		this.falledBoxes = new ArrayList<>();
		Stream.iterate(0, x -> x + 1).limit(HEIGHT).forEach(i -> {
			this.gridBoxes.put(i, new int[WIDTH]);
		});
	}

	public void addFallingFigure(Figure figure) {
		this.logger.debug("==> Engine.addFallingFigure()");
		this.fallingFigure = figure;
		addToGrid();
	}

	private void addToGrid() {
		this.fallingFigure.getBoxes().stream().forEach(b -> {
			int x = (int) Math.round(b.getX() / Box.SIZE);
			int y = (int) Math.round(b.getY() / Box.SIZE);
			int[] cells = this.gridBoxes.get(y);
			cells[x] = 1;
		});
	}

	private void removeFromGrid() {
		this.fallingFigure.getBoxes().stream().forEach(b -> {
			int x = (int) Math.round(b.getX() / Box.SIZE);
			int y = (int) Math.round(b.getY() / Box.SIZE);
			int[] cells = this.gridBoxes.get(y);
			cells[x] = 0;
		});
	}

	public void moveRight() {
		this.logger.debug("==> Engine.moveRight()");
		removeFromGrid();
		if (canMoveRight(this.fallingFigure) && noHit(this.fallingFigure, 1, 0)) {
			this.fallingFigure.moveRight();
			this.serializeUtil.convertFigureToString(this.fallingFigure)
					.ifPresent(this.kafkaService::sendMessageFigure);
		}
		addToGrid();
	}

	public void moveLeft() {
		this.logger.debug("==> Engine.moveLeft()");
		removeFromGrid();
		if (canMoveLeft(this.fallingFigure) && noHit(this.fallingFigure, -1, 0)) {
			this.fallingFigure.moveLeft();
			this.serializeUtil.convertFigureToString(this.fallingFigure)
					.ifPresent(this.kafkaService::sendMessageFigure);
		}
		addToGrid();
	}

	public void moveDown() {
		this.logger.debug("==> Engine.moveDown()");
		removeFromGrid();
		if (canMoveDown(this.fallingFigure) && noHit(this.fallingFigure, 0, 1)) {
			this.fallingFigure.moveDown();
			this.serializeUtil.convertFigureToString(this.fallingFigure)
					.ifPresent(this.kafkaService::sendMessageFigure);
		}
		addToGrid();
	}

	private boolean canMoveRight(Figure figure) {
		int maxX = figure.getBoxes().stream().mapToInt(b -> (int) Math.round(b.getX() / Box.SIZE)).max().orElse(0);
		if (maxX + 1 == WIDTH)
			return false;
		return true;
	}

	private boolean canMoveLeft(Figure figure) {
		int minX = figure.getBoxes().stream().mapToInt(b -> (int) Math.round(b.getX() / Box.SIZE)).min().orElse(0);
		if (minX == 0)
			return false;
		return true;
	}

	private boolean canMoveDown(Figure figure) {
		int maxY = figure.getBoxes().stream().mapToInt(b -> (int) Math.round(b.getY() / Box.SIZE)).max().orElse(0);
		if (maxY + 1 == HEIGHT)
			return false;
		return true;
	}

	private boolean noHit(Figure figure, int offsetX, int offsetY) {
		return figure.getBoxes().stream().allMatch(b -> {
			int x = (int) Math.round(b.getX() / Box.SIZE);
			int y = (int) Math.round(b.getY() / Box.SIZE);
			int[] cells = this.gridBoxes.get(y + offsetY);
			return cells[x + offsetX] == 0;
		});
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
		removeFromGrid();
		while (canMoveDown(this.fallingFigure) && noHit(this.fallingFigure, 0, 1)) {
			this.fallingFigure.moveDown();
		}
		addToGrid();
		this.serializeUtil.convertFigureToString(this.fallingFigure).ifPresent(this.kafkaService::sendMessageFigure);
		this.falledBoxes.addAll(this.fallingFigure.getBoxes());
		this.serializeUtil.convertListOfBoxesToString(this.falledBoxes).ifPresent(this.kafkaService::sendMessageBoard);
		this.figureService.askForNextFigure().subscribe(m -> {
			this.logger.debug("==> askForNextFigure.subscribe = " + m.getContent());
		});
	}

	private boolean rotate(int direction) {
		this.logger.debug("==> Engine.rotate()");

		int rotation = 0;

		if (this.fallingFigure.numRotations == 2) {
			if (this.fallingFigure.rotation == 0) {
				rotation = this.fallingFigure.rotation + 1;
			} else {
				direction *= -1;
				rotation = 0;
			}
		} else {
			rotation = this.fallingFigure.rotation + 1;
			rotation = rotation % this.fallingFigure.numRotations;
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
		removeFromGrid();
		if (noHit(tmpFigure, 0, 0)) {
			this.fallingFigure.rotation = rotation;
			this.fallingFigure.setBoxes(tmpFigure.getBoxes());
			this.fallingFigure.setCenter(tmpFigure.getCenter());
		}
		addToGrid();
		return true;
	}

}