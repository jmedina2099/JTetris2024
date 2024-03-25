package org.jmedina.jtetris.engine.tool;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
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
	private Map<Integer, Integer[]> gridBoxes = new HashMap<>();

	public void start() {
		this.logger.debug("==> Engine.start()");
		this.falledBoxes = new ArrayList<>();
		Stream.iterate(0, x -> x + 1).limit(HEIGHT).forEach(i -> {
			Integer[] array = new Integer[WIDTH];
			Arrays.fill(array, 0);
			this.gridBoxes.put(i, array);
		});
	}

	public void addFallingFigure(Figure figure) {
		this.logger.debug("==> Engine.addFallingFigure()");
		this.fallingFigure = figure;
		addToGrid(figure);
	}

	private void addToGrid(Figure figure) {
		figure.getBoxes().stream().forEach(b -> {
			int x = (int) Math.round(b.getX() / Box.SIZE);
			int y = (int) Math.round(b.getY() / Box.SIZE);
			Integer[] cells = this.gridBoxes.get(y);
			cells[x] = 1;
		});
	}

	private void removeFromGrid(Figure figure) {
		figure.getBoxes().stream().forEach(b -> {
			int x = (int) Math.round(b.getX() / Box.SIZE);
			int y = (int) Math.round(b.getY() / Box.SIZE);
			Integer[] cells = this.gridBoxes.get(y);
			cells[x] = 0;
		});
	}

	public Optional<Box[]> moveRight() {
		this.logger.debug("==> Engine.moveRight()");
		Figure figureTmp = this.fallingFigure.clone();
		removeFromGrid(figureTmp);
		if (canMoveRight(figureTmp) && noHit(figureTmp, 1, 0)) {
			figureTmp.moveRight();
			// this.serializeUtil.convertFigureToString(figureTmp).ifPresent(this.kafkaService::sendMessageFigure);
			this.fallingFigure = figureTmp;
			addToGrid(figureTmp);
			return Optional.of(figureTmp.getBoxes().toArray(new Box[0]));
		}
		addToGrid(this.fallingFigure);
		return Optional.empty();
	}

	public Optional<Box[]> moveLeft() {
		this.logger.debug("==> Engine.moveLeft()");
		Figure figureTmp = this.fallingFigure.clone();
		removeFromGrid(figureTmp);
		if (canMoveLeft(figureTmp) && noHit(figureTmp, -1, 0)) {
			figureTmp.moveLeft();
			// this.serializeUtil.convertFigureToString(figureTmp).ifPresent(this.kafkaService::sendMessageFigure);
			this.fallingFigure = figureTmp;
			addToGrid(figureTmp);
			return Optional.of(figureTmp.getBoxes().toArray(new Box[0]));
		}
		addToGrid(this.fallingFigure);
		return Optional.empty();
	}

	public Optional<Box[]> moveDown() {
		this.logger.debug("==> Engine.moveDown()");
		Figure figureTmp = this.fallingFigure.clone();
		removeFromGrid(figureTmp);
		if (canMoveDown(figureTmp) && noHit(figureTmp, 0, 1)) {
			figureTmp.moveDown();
			// this.serializeUtil.convertFigureToString(figureTmp).ifPresent(this.kafkaService::sendMessageFigure);
			this.fallingFigure = figureTmp;
			addToGrid(figureTmp);
			return Optional.of(figureTmp.getBoxes().toArray(new Box[0]));
		}
		addToGrid(this.fallingFigure);
		return Optional.empty();
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
			Integer[] cells = this.gridBoxes.get(y + offsetY);
			return cells[x + offsetX] == 0;
		});
	}

	public Optional<Box[]> rotateRight() {
		this.logger.debug("==> Engine.rotateRight()");

		if (this.fallingFigure.numRotations == 0) {
			return Optional.empty();
		}

		Figure figureTmp = this.fallingFigure.clone();
		removeFromGrid(figureTmp);

		int direction = figureTmp.numRotations == 2 ? -1 : 1;
		if (rotate(figureTmp, direction)) {
			// this.serializeUtil.convertFigureToString(figureTmp).ifPresent(this.kafkaService::sendMessageFigure);
			this.fallingFigure = figureTmp;
			addToGrid(figureTmp);
			return Optional.of(figureTmp.getBoxes().toArray(new Box[0]));
		}
		addToGrid(this.fallingFigure);
		return Optional.empty();
	}

	public Optional<Box[]> rotateLeft() {
		this.logger.debug("==> Engine.rotateLeft()");

		if (this.fallingFigure.numRotations == 0) {
			return Optional.empty();
		}

		Figure figureTmp = this.fallingFigure.clone();
		removeFromGrid(figureTmp);

		int direction = -1;
		if (rotate(figureTmp, direction)) {
			// this.serializeUtil.convertFigureToString(figureTmp).ifPresent(this.kafkaService::sendMessageFigure);
			this.fallingFigure = figureTmp;
			addToGrid(figureTmp);
			return Optional.of(figureTmp.getBoxes().toArray(new Box[0]));
		}
		addToGrid(this.fallingFigure);
		return Optional.empty();
	}

	public Optional<Box[]> bottomDown() {
		this.logger.debug("==> Engine.bottomDown()");

		Figure figureTmp = this.fallingFigure.clone();
		removeFromGrid(figureTmp);
		while (canMoveDown(figureTmp) && noHit(figureTmp, 0, 1)) {
			figureTmp.moveDown();
		}
		addToGrid(figureTmp);
		this.falledBoxes.addAll(figureTmp.getBoxes());
		this.serializeUtil.convertListOfBoxesToString(this.falledBoxes).ifPresent(this.kafkaService::sendMessageBoard);

		this.fallingFigure = new Figure();
		// this.serializeUtil.convertFigureToString(this.fallingFigure).ifPresent(this.kafkaService::sendMessageFigure);

		runInThread(figureTmp);

		return Optional.of(figureTmp.getBoxes().toArray(new Box[0]));
	}

	private void runInThread(Figure figureTmp) {
		Runnable runnable = () -> {
			if (checkMakeLines(figureTmp) > 0) {
				this.serializeUtil.convertListOfBoxesToString(this.falledBoxes)
						.ifPresent(this.kafkaService::sendMessageBoard);
			}
			this.figureService.askForNextFigure().subscribe(m -> {
				this.logger.debug("==> askForNextFigure.subscribe = " + m.getContent());
			});
		};
		Thread thread = new Thread(runnable);
		thread.start();
	}

	private int checkMakeLines(Figure figure) {
		int minY = figure.getBoxes().stream().mapToInt(b -> (int) Math.round(b.getY() / Box.SIZE)).min().orElse(0);
		int maxY = figure.getBoxes().stream().mapToInt(b -> (int) Math.round(b.getY() / Box.SIZE)).max().orElse(0);

		int[] numLinesCompleted = new int[] { 0 };

		AtomicReference<Integer[]> cells = new AtomicReference<Integer[]>();
		AtomicReference<Boolean> isLineCompleted = new AtomicReference<Boolean>();
		AtomicReference<List<Box>> boxesOnLine = new AtomicReference<List<Box>>();

		int[] indexY = new int[] { maxY };
		Stream.iterate(0, x -> x + 1).limit(maxY - minY + 1).forEach(i -> {
			cells.set(this.gridBoxes.get(indexY[0]));
			isLineCompleted.set(Arrays.asList(cells.get()).stream().allMatch(c -> c == 1));
			if (isLineCompleted.get()) {
				numLinesCompleted[0]++;
				boxesOnLine.set(this.falledBoxes.stream()
						.filter(b -> (int) Math.round(b.getY() / Box.SIZE) == indexY[0]).collect(Collectors.toList()));
				this.falledBoxes.removeAll(boxesOnLine.get());
				this.gridBoxes.remove(indexY[0]);
				moveDownFalledBoxes(indexY[0]);
			} else {
				indexY[0]--;
			}
		});
		return numLinesCompleted[0];
	}

	private void moveDownFalledBoxes(int indexY) {
		this.logger.debug("==> Engine.rotate() indexY={}", indexY);
		this.falledBoxes.stream().forEach(b -> {
			if ((int) Math.round(b.getY() / Box.SIZE) < indexY) {
				b.moveDown();
			}
		});
		List<Integer> keys = this.gridBoxes.keySet().stream().filter(y -> y < indexY).collect(Collectors.toList());
		Collections.sort(keys, Collections.reverseOrder());
		keys.stream().forEach(y -> {
			this.gridBoxes.put(y + 1, this.gridBoxes.get(y));
		});
		Integer[] array = new Integer[WIDTH];
		Arrays.fill(array, 0);
		this.gridBoxes.put(0, array);
	}

	private boolean rotate(Figure figure, int direction) {
		this.logger.debug("==> Engine.rotate()");

		int rotation = 0;
		if (figure.numRotations == 2) {
			if (figure.rotation == 0) {
				rotation = figure.rotation + 1;
			} else {
				direction *= -1;
				rotation = 0;
			}
		} else {
			rotation = figure.rotation + 1;
			rotation = rotation % figure.numRotations;
		}

		this.logger.debug("==> center = {}", figure.getCenter());

		RotationUtil.rotateFigure(figure, direction);
		double max = figure.getBoxes().stream().mapToDouble(b -> b.getX()).max().getAsDouble();
		double size = Box.SIZE;
		while ((max + size) > size * 10) {
			figure.moveLeft();
			max = figure.getBoxes().stream().mapToDouble(b -> b.getX()).max().getAsDouble();
		}
		double min = figure.getBoxes().stream().mapToDouble(b -> b.getX()).min().getAsDouble();
		while (min < 0) {
			figure.moveRight();
			min = figure.getBoxes().stream().mapToDouble(b -> b.getX()).min().getAsDouble();
		}
		if (noHit(figure, 0, 0)) {
			figure.rotation = rotation;
			return true;
		}

		return false;
	}

}