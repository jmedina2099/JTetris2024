package org.jmedina.jtetris.engine.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jmedina.jtetris.common.enumeration.BoardOperationEnumeration;
import org.jmedina.jtetris.common.enumeration.FigureOperationEnumeration;
import org.jmedina.jtetris.common.model.BoardOperation;
import org.jmedina.jtetris.common.model.FigureOperation;
import org.jmedina.jtetris.engine.enumeration.MoveDirectionEnumeration;
import org.jmedina.jtetris.engine.figure.BoxMotion;
import org.jmedina.jtetris.engine.figure.FigureMotion;
import org.jmedina.jtetris.engine.model.NextFigureOperation;
import org.jmedina.jtetris.engine.publisher.BoardPublisher;
import org.jmedina.jtetris.engine.publisher.EnginePublisher;
import org.jmedina.jtetris.engine.publisher.NextFigurePublisher;
import org.jmedina.jtetris.engine.service.EngineService;
import org.jmedina.jtetris.engine.service.GridSupportService;
import org.jmedina.jtetris.engine.service.KafkaService;
import org.jmedina.jtetris.engine.util.RotationUtil;
import org.jmedina.jtetris.engine.util.SerializeUtil;
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

	private final Logger logger = LogManager.getLogger(this.getClass());
	private final BoardPublisher boardPublisher;
	private final GridSupportService gridSupport;
	private final RotationUtil rotationUtil;
	private final SerializeUtil serializeUtil;
	private FigureMotion fallingFigure;
	private long initialTimeStamp;
	private List<BoxMotion> falledBoxes;
	private ReentrantLock lock = new ReentrantLock();
	private NextFigurePublisher nextFigurePublisher;
	private EnginePublisher enginePublisher;

	@Autowired(required = false)
	private KafkaService kafkaService;

	@Value("${use.kafka}")
	private boolean useKafka;

	private boolean isRunning = false;

	public static final int WIDTH = 10;
	public static final int HEIGHT = 20;

	@Override
	public void start(NextFigurePublisher nextFigurePublisher, EnginePublisher enginePublisher) {
		this.logger.debug("==> Engine.start()");
		this.isRunning = true;
		this.nextFigurePublisher = nextFigurePublisher;
		this.enginePublisher = enginePublisher;
		reset();
	}

	@Override
	public void stop() {
		this.logger.debug("==> Engine.stop()");
		this.isRunning = false;
		this.gridSupport.initializeGrid();
		reset();
	}

	@Override
	public void addFigureOperation(FigureOperation<FigureMotion> figureOperation) {
		this.logger.debug("==> Engine.addFigureOperation() = " + figureOperation);
		if (this.isRunning
				&& (this.fallingFigure == null || this.initialTimeStamp < figureOperation.getInitialTimeStamp())) {
			this.logger.debug("==> Engine.addFallingFigure.adding.. " + figureOperation.getInitialTimeStamp());
			this.fallingFigure = (FigureMotion) figureOperation.getFigure();
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
		FigureMotion figureRef = this.fallingFigure;
		this.fallingFigure = null;
		try {
			while (canMoveDown(figureRef) && this.gridSupport.noHit(figureRef, 0, 1)) {
				figureRef.moveDown();
				sendAsyncEventsForFigureOperation(getFigureOperationForMovement(figureRef.clone()));
			}
			this.gridSupport.addToGrid(figureRef);
			this.falledBoxes.addAll(figureRef.getBoxes());
			sendAsyncEventsForBoardOperation(
					getBoardOperation(cloneBoxes(this.falledBoxes), BoardOperationEnumeration.BOARD_WITH_ADDED_FIGURE));

			int numLinesMaded = 0;
			if ((numLinesMaded = checkMakeLines(figureRef)) > 0) {
				sendAsyncEventsForBoardOperation(getBoardOperation(cloneBoxes(this.falledBoxes),
						BoardOperationEnumeration.getByNumLinesMaded(numLinesMaded)));
			}
			this.nextFigurePublisher.sendNextFigurePetition(
					NextFigureOperation.builder().operation(FigureOperationEnumeration.NEW_OPERATION).build());
			return Optional.of(true);
		} catch (Exception e) {
			this.logger.error("=*=> ERROR: ", e);
		} finally {
			lock.unlock();
		}
		return Optional.of(false);
	}

	private List<BoxMotion> cloneBoxes(List<BoxMotion> falledBoxes) {
		return falledBoxes.stream().map(b -> b.clone()).collect(Collectors.toList());
	}

	private void reset() {
		this.fallingFigure = null;
		this.falledBoxes = new ArrayList<>();
	}

	private void sendFigureOperationToKafka(FigureOperation<FigureMotion> op) {
		this.logger.debug("==> Engine.sendFigureOperationToKafka() = " + this.kafkaService);
		if (this.useKafka) {
			this.serializeUtil.convertFigureOperationToString(op).ifPresent(this.kafkaService::sendMessageFigure);
		}
	}

	private void sendBoardToKafka(BoardOperation<BoxMotion> board) {
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
				sendAsyncEventsForFigureOperation(getFigureOperationForMovement(this.fallingFigure.clone()));
				return Optional.of(true);
			}
		} catch (Exception e) {
			this.logger.error("=*=> ERROR = ", e);
		} finally {
			lock.unlock();
		}
		return Optional.of(false);
	}

	private void sendAsyncEventsForFigureOperation(FigureOperation<FigureMotion> figureOperation) {
		this.enginePublisher.sendFigureOperation(figureOperation);
		sendFigureOperationToKafka(figureOperation);
	}

	private void sendAsyncEventsForBoardOperation(BoardOperation<BoxMotion> board) {
		this.boardPublisher.sendBoard(board);
		sendBoardToKafka(board);
	}

	private FigureOperation<FigureMotion> getFigureOperationForMovement(FigureMotion figure) {
		return FigureOperation.<FigureMotion>builder().operation(FigureOperationEnumeration.MOVEMENT_OPERATION)
				.figure(figure).initialTimeStamp(this.initialTimeStamp).timeStamp(System.nanoTime()).build();
	}

	private FigureOperation<FigureMotion> getFigureOperationForRotation(FigureMotion figure) {
		return FigureOperation.<FigureMotion>builder().operation(FigureOperationEnumeration.ROTATION_OPERATION)
				.figure(figure).initialTimeStamp(this.initialTimeStamp).timeStamp(System.nanoTime()).build();
	}

	private BoardOperation<BoxMotion> getBoardOperation(List<BoxMotion> boxes, BoardOperationEnumeration op) {
		return BoardOperation.<BoxMotion>builder().operation(op).boxes(boxes).timeStamp(System.nanoTime()).build();
	}

	private boolean canMoveRight(FigureMotion figure) {
		int maxX = figure.getBoxes().stream().mapToInt(b -> (int) Math.round(b.getX() / BoxMotion.SIZE)).max()
				.orElse(0);
		if (maxX + 1 == WIDTH)
			return false;
		return true;
	}

	private boolean canMoveLeft(FigureMotion figure) {
		int minX = figure.getBoxes().stream().mapToInt(b -> (int) Math.round(b.getX() / BoxMotion.SIZE)).min()
				.orElse(0);
		if (minX == 0)
			return false;
		return true;
	}

	private boolean canMoveDown(FigureMotion figure) {
		int maxY = figure.getBoxes().stream().mapToInt(b -> (int) Math.round(b.getY() / BoxMotion.SIZE)).max()
				.orElse(0);
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
			FigureMotion figureTmp = this.fallingFigure.clone();
			if (rotateHelper(figureTmp, direction)) {
				this.fallingFigure = figureTmp;
				sendAsyncEventsForFigureOperation(getFigureOperationForRotation(this.fallingFigure.clone()));
				return Optional.of(true);
			}
		} catch (Exception e) {
			this.logger.error("=*=> ERROR = ", e);
		} finally {
			lock.unlock();
		}
		return Optional.of(false);
	}

	private boolean rotateHelper(FigureMotion figure, int direction) {
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

	private void tryToFit(FigureMotion figure) {
		double max = figure.getBoxes().stream().mapToDouble(b -> b.getX()).max().getAsDouble();
		double size = BoxMotion.SIZE;
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

	private int checkMakeLines(FigureMotion figure) {
		int minY = figure.getBoxes().stream().mapToInt(b -> (int) Math.round(b.getY() / BoxMotion.SIZE)).min()
				.orElse(0);
		int maxY = figure.getBoxes().stream().mapToInt(b -> (int) Math.round(b.getY() / BoxMotion.SIZE)).max()
				.orElse(0);

		int[] numLinesCompleted = new int[] { 0 };

		AtomicReference<Stream<Boolean>> stream = new AtomicReference<Stream<Boolean>>();
		AtomicReference<Boolean> isLineCompleted = new AtomicReference<Boolean>();
		AtomicReference<List<BoxMotion>> boxesOnLine = new AtomicReference<List<BoxMotion>>();

		int[] indexY = new int[] { maxY };
		Stream.iterate(0, x -> x + 1).limit(maxY - minY + 1).forEach(i -> {
			stream.set(this.gridSupport.getStreamRow(indexY[0]));
			isLineCompleted.set(stream.get().allMatch(c -> c));
			if (isLineCompleted.get()) {
				numLinesCompleted[0]++;
				boxesOnLine.set(
						this.falledBoxes.stream().filter(b -> (int) Math.round(b.getY() / BoxMotion.SIZE) == indexY[0])
								.collect(Collectors.toList()));
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
			if ((int) Math.round(b.getY() / BoxMotion.SIZE) < indexY) {
				b.moveDown();
			}
		});
		this.gridSupport.moveDownGrid(indexY);
	}

}