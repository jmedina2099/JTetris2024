package org.jmedina.jtetris.engine.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jmedina.jtetris.engine.enumeration.MoveDirectionEnumeration;
import org.jmedina.jtetris.engine.figure.Box;
import org.jmedina.jtetris.engine.figure.Figure;
import org.jmedina.jtetris.engine.model.Board;
import org.jmedina.jtetris.engine.publisher.EnginePublisher;
import org.jmedina.jtetris.engine.publisher.FigurePublisher;
import org.jmedina.jtetris.engine.service.EngineService;
import org.jmedina.jtetris.engine.service.GridSupportService;
import org.jmedina.jtetris.engine.service.KafkaService;
import org.jmedina.jtetris.engine.util.RotationUtil;
import org.jmedina.jtetris.engine.util.SerializeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

/**
 * @author Jorge Medina
 *
 */
@RequiredArgsConstructor
@Service
public class EngineServiceImpl implements EngineService {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private final FigurePublisher figurePublisher;
	private final EnginePublisher enginePublisher;
	private final KafkaService kafkaService;
	private final GridSupportService gridSupport;
	private final RotationUtil rotationUtil;
	private final SerializeUtil serializeUtil;
	private Figure fallingFigure;
	private List<Box> falledBoxes;
	private ReentrantLock lock = new ReentrantLock();

	public static final int WIDTH = 10;
	public static final int HEIGHT = 20;

	@Override
	public void start() {
		this.logger.debug("==> Engine.start()");
		this.fallingFigure = null;
		this.falledBoxes = new ArrayList<>();
		this.gridSupport.initializeGrid();
	}

	@Override
	public void addFallingFigure(Figure figure) {
		this.logger.debug("==> Engine.addFallingFigure()");
		if (this.fallingFigure == null || this.fallingFigure.getInitialTimeStamp() < figure.getInitialTimeStamp()) {
			this.logger.debug("==> Engine.addFallingFigure.adding.. " + figure.getInitialTimeStamp());
			this.fallingFigure = figure;
			this.gridSupport.addToGrid(this.fallingFigure);
		}
	}

	@Override
	public Optional<Boolean> moveRight() {
		this.logger.debug("==> Engine.moveRight()");
		return moveFigure(MoveDirectionEnumeration.RIGHT);
	}

	@Override
	public Optional<Boolean> moveLeft() {
		this.logger.debug("==> Engine.moveLeft()");
		return moveFigure(MoveDirectionEnumeration.LEFT);
	}

	@Override
	public Optional<Boolean> rotateRight() {
		this.logger.debug("==> Engine.rotateRight()");
		int direction = this.fallingFigure.numRotations == 2 ? -1 : 1;
		return rotate(direction);
	}

	@Override
	public Optional<Boolean> rotateLeft() {
		this.logger.debug("==> Engine.rotateLeft()");
		int direction = -1;
		return rotate(direction);
	}

	@Override
	public Optional<Board> bottomDown() {
		this.logger.debug("==> Engine.bottomDown()");
		if (Objects.isNull(this.fallingFigure)) {
			return Optional.empty();
		}
		boolean isLockAcquired = lock.tryLock();
		Board board = null;
		if (isLockAcquired) {
			try {
				this.gridSupport.removeFromGrid(this.fallingFigure);
				while (canMoveDown(this.fallingFigure) && this.gridSupport.noHit(this.fallingFigure, 0, 1)) {
					this.fallingFigure.moveDown();
					this.fallingFigure.setTimeStamp(System.nanoTime());
					this.enginePublisher.sendMovementFigure(this.fallingFigure);
					this.serializeUtil.convertFigureToString(this.fallingFigure)
							.ifPresent(this.kafkaService::sendMessageFigure);
				}
				this.gridSupport.addToGrid(this.fallingFigure);
				this.falledBoxes.addAll(this.fallingFigure.getBoxes());
				board = new Board(this.falledBoxes, System.nanoTime());
				this.serializeUtil.convertBoardToString(board).ifPresent(this.kafkaService::sendMessageBoard);

				if (checkMakeLines(this.fallingFigure) > 0) {
					board = new Board(this.falledBoxes, System.nanoTime());
					this.serializeUtil.convertBoardToString(board).ifPresent(this.kafkaService::sendMessageBoard);
				}
				this.fallingFigure = null;
				this.figurePublisher.askForNextFigure();
			} finally {
				lock.unlock();
			}
		}
		return Optional.of(board);
	}

	private Optional<Boolean> moveFigure(MoveDirectionEnumeration direction) {
		if (Objects.isNull(this.fallingFigure)) {
			return Optional.of(false);
		}
		boolean isLockAcquired;
		try {
			isLockAcquired = lock.tryLock(1, TimeUnit.SECONDS);
			if (isLockAcquired) {
				this.gridSupport.removeFromGrid(this.fallingFigure);
				if ((direction.equals(MoveDirectionEnumeration.LEFT) ? canMoveLeft(this.fallingFigure)
						: canMoveRight(this.fallingFigure))
						&& this.gridSupport.noHit(this.fallingFigure, direction.getOffset(), 0)) {
					if (direction.equals(MoveDirectionEnumeration.LEFT)) {
						this.fallingFigure.moveLeft();
					} else if (direction.equals(MoveDirectionEnumeration.RIGHT)) {
						this.fallingFigure.moveRight();
					}
					this.fallingFigure.setTimeStamp(System.nanoTime());
					this.enginePublisher.sendMovementFigure(this.fallingFigure);
					this.serializeUtil.convertFigureToString(this.fallingFigure)
							.ifPresent(this.kafkaService::sendMessageFigure);
					return Optional.of(true);
				}
			}
		} catch (Exception e) {
			this.logger.error("=*=> ERROR = ", e);
		} finally {
			this.gridSupport.addToGrid(this.fallingFigure);
			lock.unlock();
		}
		return Optional.of(false);
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

	private Optional<Boolean> rotate(int direction) {
		if (Objects.isNull(this.fallingFigure)) {
			return Optional.of(false);
		}
		if (this.fallingFigure.numRotations == 0) {
			return Optional.of(false);
		}

		boolean isLockAcquired = false;
		try {
			isLockAcquired = lock.tryLock(1, TimeUnit.SECONDS);
			if (isLockAcquired) {
				this.gridSupport.removeFromGrid(this.fallingFigure);
				Figure figureTmp = this.fallingFigure.clone();
				if (rotateHelper(figureTmp, direction)) {
					this.fallingFigure = figureTmp;
					this.fallingFigure.setTimeStamp(System.nanoTime());
					this.enginePublisher.sendMovementFigure(this.fallingFigure);
					this.serializeUtil.convertFigureToString(figureTmp).ifPresent(this.kafkaService::sendMessageFigure);
					return Optional.of(true);
				}
			}
		} catch (Exception e) {
			this.logger.error("=*=> ERROR = ", e);
		} finally {
			this.gridSupport.addToGrid(this.fallingFigure);
			lock.unlock();
		}
		return Optional.of(false);
	}

	private boolean rotateHelper(Figure figure, int direction) {
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

		this.rotationUtil.rotateFigure(figure, direction);
		tryToFit(figure);
		if (this.gridSupport.noHit(figure, 0, 0)) {
			figure.rotation = rotation;
			return true;
		}

		return false;
	}

	private void tryToFit(Figure figure) {
		double max = figure.getBoxes().stream().mapToDouble(b -> b.getX()).max().getAsDouble();
		double size = Box.SIZE;
		int numOps = 0;
		while ((max + size) > size * 10 && numOps < 3) {
			figure.moveLeft();
			max = figure.getBoxes().stream().mapToDouble(b -> b.getX()).max().getAsDouble();
			numOps++;
		}
		double min = figure.getBoxes().stream().mapToDouble(b -> b.getX()).min().getAsDouble();
		numOps = 0;
		while (min < 0 && numOps < 3) {
			figure.moveRight();
			min = figure.getBoxes().stream().mapToDouble(b -> b.getX()).min().getAsDouble();
			numOps++;
		}
	}

	private int checkMakeLines(Figure figure) {
		int minY = figure.getBoxes().stream().mapToInt(b -> (int) Math.round(b.getY() / Box.SIZE)).min().orElse(0);
		int maxY = figure.getBoxes().stream().mapToInt(b -> (int) Math.round(b.getY() / Box.SIZE)).max().orElse(0);

		int[] numLinesCompleted = new int[] { 0 };

		AtomicReference<Stream<Boolean>> stream = new AtomicReference<Stream<Boolean>>();
		AtomicReference<Boolean> isLineCompleted = new AtomicReference<Boolean>();
		AtomicReference<List<Box>> boxesOnLine = new AtomicReference<List<Box>>();

		int[] indexY = new int[] { maxY };
		Stream.iterate(0, x -> x + 1).limit(maxY - minY + 1).forEach(i -> {
			stream.set(this.gridSupport.getStreamRow(indexY[0]));
			isLineCompleted.set(stream.get().allMatch(c -> c));
			if (isLineCompleted.get()) {
				numLinesCompleted[0]++;
				boxesOnLine.set(this.falledBoxes.stream()
						.filter(b -> (int) Math.round(b.getY() / Box.SIZE) == indexY[0]).collect(Collectors.toList()));
				this.falledBoxes.removeAll(boxesOnLine.get());
				this.gridSupport.removeGridRow(indexY[0]);
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
		this.gridSupport.moveDownGrid(indexY);
	}

}