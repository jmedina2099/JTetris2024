package org.jmedina.jtetris.engine.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jmedina.jtetris.engine.enumeration.BoardOperationEnumeration;
import org.jmedina.jtetris.engine.enumeration.FigureOperationEnumeration;
import org.jmedina.jtetris.engine.enumeration.MoveDirectionEnumeration;
import org.jmedina.jtetris.engine.figure.Box;
import org.jmedina.jtetris.engine.figure.Figure;
import org.jmedina.jtetris.engine.model.BoardOperation;
import org.jmedina.jtetris.engine.model.FigureOperation;
import org.jmedina.jtetris.engine.publisher.BoardPublisher;
import org.jmedina.jtetris.engine.publisher.EnginePublisher;
import org.jmedina.jtetris.engine.publisher.FigurePublisher;
import org.jmedina.jtetris.engine.service.EngineService;
import org.jmedina.jtetris.engine.service.GridSupportService;
import org.jmedina.jtetris.engine.service.KafkaService;
import org.jmedina.jtetris.engine.util.RotationUtil;
import org.jmedina.jtetris.engine.util.SerializeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
	private final BoardPublisher boardPublisher;
	private final GridSupportService gridSupport;
	private final RotationUtil rotationUtil;
	private final SerializeUtil serializeUtil;
	private Figure fallingFigure;
	private long initialTimeStamp;
	private List<Box> falledBoxes;
	private ReentrantLock lock = new ReentrantLock();
	private FigurePublisher figurePublisher;
	private EnginePublisher enginePublisher;

	@Autowired(required = false)
	private KafkaService kafkaService;

	@Value("${use.kafka}")
	private boolean useKafka;

	private boolean isRunning = false;

	public static final int WIDTH = 10;
	public static final int HEIGHT = 20;

	@Override
	public void start(FigurePublisher figurePublisher, EnginePublisher enginePublisher) {
		this.logger.debug("==> Engine.start()");
		this.isRunning = true;
		this.figurePublisher = figurePublisher;
		this.enginePublisher = enginePublisher;
		reset();
	}

	@Override
	public void stop() {
		this.logger.debug("==> Engine.stop()");
		this.isRunning = false;
		if (Objects.nonNull(this.figurePublisher)) {
			this.figurePublisher.stop();
		}
		if (Objects.nonNull(this.enginePublisher)) {
			this.enginePublisher.stop();
		}
		this.boardPublisher.stop();
		reset();
	}

	@Override
	public void addFigureOperation(FigureOperation figureOperation) {
		this.logger.debug("==> Engine.addFigureOperation() = " + figureOperation);
		if (this.isRunning
				&& (this.fallingFigure == null || this.initialTimeStamp < figureOperation.getInitialTimeStamp())) {
			this.logger.debug("==> Engine.addFallingFigure.adding.. " + figureOperation.getInitialTimeStamp());
			this.fallingFigure = figureOperation.getFigure();
			this.initialTimeStamp = figureOperation.getInitialTimeStamp();
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
		if (Objects.isNull(this.fallingFigure)) {
			return Optional.of(false);
		}
		int direction = this.fallingFigure.numRotations == 2 ? -1 : 1;
		return rotate(direction);
	}

	@Override
	public Optional<Boolean> rotateLeft() {
		this.logger.debug("==> Engine.rotateLeft()");
		if (Objects.isNull(this.fallingFigure)) {
			return Optional.of(false);
		}
		int direction = -1;
		return rotate(direction);
	}

	@Override
	public Optional<Boolean> bottomDown() {
		this.logger.debug("==> Engine.bottomDown()");
		if (Objects.isNull(this.fallingFigure)) {
			return Optional.of(false);
		}
		boolean isLockAcquired = lock.tryLock();
		if (!isLockAcquired) {
			return Optional.of(false);
		}
		Figure figureRef = this.fallingFigure;
		this.fallingFigure = null;
		try {
			while (canMoveDown(figureRef) && this.gridSupport.noHit(figureRef, 0, 1)) {
				figureRef.moveDown();
				sendAsyncEventsForFigureOperation(getFigureOperationForMovement(figureRef.clone()));
			}
			this.gridSupport.addToGrid(figureRef);
			this.falledBoxes.addAll(figureRef.getBoxes());
			sendAsyncEventsForBoardOperation(
					getBoardOperation(this.falledBoxes, BoardOperationEnumeration.BOARD_WITH_ADDED_FIGURE));

			int numLinesMaded = 0;
			if ((numLinesMaded = checkMakeLines(figureRef)) > 0) {
				sendAsyncEventsForBoardOperation(getBoardOperation(this.falledBoxes,
						BoardOperationEnumeration.getByNumLinesMaded(numLinesMaded)));
			}
			this.figurePublisher.getNextFigure();
			return Optional.of(true);
		} catch (Exception e) {
			this.logger.error("=*=> ERROR: ", e);
		} finally {
			lock.unlock();
		}
		return Optional.of(false);
	}

	private void reset() {
		this.fallingFigure = null;
		this.falledBoxes = new ArrayList<>();
		this.gridSupport.initializeGrid();
	}

	private void sendFigureOperationToKafka(FigureOperation op) {
		this.logger.debug("==> Engine.sendFigureOperationToKafka() = " + this.kafkaService);
		if (this.useKafka) {
			this.serializeUtil.convertFigureOperationToString(op).ifPresent(this.kafkaService::sendMessageFigure);
		}
	}

	private void sendBoardToKafka(BoardOperation board) {
		this.logger.debug("==> Engine.sendBoardToKafka() = " + this.kafkaService);
		if (this.useKafka) {
			this.serializeUtil.convertBoardToString(board).ifPresent(this.kafkaService::sendMessageBoard);
		}
	}

	private Optional<Boolean> moveFigure(MoveDirectionEnumeration direction) {
		if (Objects.isNull(this.fallingFigure)) {
			return Optional.of(false);
		}
		boolean isLockAcquired = lock.tryLock();
		if (!isLockAcquired) {
			return Optional.of(false);
		}
		try {
			if ((direction.equals(MoveDirectionEnumeration.LEFT) ? canMoveLeft(this.fallingFigure)
					: canMoveRight(this.fallingFigure))
					&& this.gridSupport.noHit(this.fallingFigure, direction.getOffset(), 0)) {
				if (direction.equals(MoveDirectionEnumeration.LEFT)) {
					this.fallingFigure.moveLeft();
				} else if (direction.equals(MoveDirectionEnumeration.RIGHT)) {
					this.fallingFigure.moveRight();
				}
				sendAsyncEventsForFigureOperation(getFigureOperationForMovement(this.fallingFigure));
				return Optional.of(true);
			}
		} catch (Exception e) {
			this.logger.error("=*=> ERROR = ", e);
		} finally {
			lock.unlock();
		}
		return Optional.of(false);
	}

	private void sendAsyncEventsForFigureOperation(FigureOperation figureOperation) {
		this.enginePublisher.sendFigureOperation(figureOperation);
		sendFigureOperationToKafka(figureOperation);
	}

	private void sendAsyncEventsForBoardOperation(BoardOperation board) {
		this.boardPublisher.sendBoard(board);
		sendBoardToKafka(board);
	}

	private FigureOperation getFigureOperationForMovement(Figure figure) {
		return FigureOperation.builder().operation(FigureOperationEnumeration.MOVEMENT_OPERATION).figure(figure)
				.initialTimeStamp(this.initialTimeStamp).timeStamp(System.nanoTime()).build();
	}

	private FigureOperation getFigureOperationForRotation(Figure figure) {
		return FigureOperation.builder().operation(FigureOperationEnumeration.ROTATION_OPERATION).figure(figure)
				.initialTimeStamp(this.initialTimeStamp).timeStamp(System.nanoTime()).build();
	}

	private BoardOperation getBoardOperation(List<Box> boxes, BoardOperationEnumeration op) {
		return BoardOperation.builder().operation(op).boxes(boxes).timeStamp(System.nanoTime()).build();
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
		if (this.fallingFigure.numRotations == 0) {
			return Optional.of(false);
		}
		boolean isLockAcquired = lock.tryLock();
		if (!isLockAcquired) {
			return Optional.of(false);
		}
		try {
			Figure figureTmp = this.fallingFigure.clone();
			if (rotateHelper(figureTmp, direction)) {
				this.fallingFigure = figureTmp;
				sendAsyncEventsForFigureOperation(getFigureOperationForRotation(this.fallingFigure));
				return Optional.of(true);
			}
		} catch (Exception e) {
			this.logger.error("=*=> ERROR = ", e);
		} finally {
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