package org.jmedina.jtetris.engine.service.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jmedina.jtetris.engine.figure.BoxMotion;
import org.jmedina.jtetris.engine.figure.FigureMotion;
import org.jmedina.jtetris.engine.service.GridSupportService;

/**
 * @author Jorge Medina
 *
 */
public class GridSupportServiceImpl implements GridSupportService {

	private final Logger logger = LogManager.getLogger(this.getClass());
	private Map<Integer, boolean[]> gridBoxes = new HashMap<>();

	public GridSupportServiceImpl() {
		initializeGrid();
	}

	@Override
	public void initializeGrid() {
		this.logger.debug("==> GridSupportService.initializeGrid()");
		Stream.iterate(0, x -> x + 1).limit(EngineServiceImpl.HEIGHT).forEach(i -> {
			this.gridBoxes.put(i, new boolean[EngineServiceImpl.WIDTH]);
		});
	}

	@Override
	public void addToGrid(FigureMotion<BoxMotion> figure) {
		figure.getBoxes().stream().forEach(b -> {
			int x = (int) Math.round(b.getX() / BoxMotion.SIZE);
			int y = (int) Math.round(b.getY() / BoxMotion.SIZE);
			this.gridBoxes.get(y)[x] = true;
		});
	}

	@Override
	public void removeFromGrid(FigureMotion<BoxMotion> figure) {
		figure.getBoxes().stream().forEach(b -> {
			int x = (int) Math.round(b.getX() / BoxMotion.SIZE);
			int y = (int) Math.round(b.getY() / BoxMotion.SIZE);
			this.gridBoxes.get(y)[x] = false;
		});
	}

	@Override
	public Stream<Boolean> getStreamRow(int indexY) {
		boolean[] row = this.gridBoxes.get(indexY);
		return IntStream.range(0, row.length).mapToObj(idx -> row[idx]);
	}

	@Override
	public void removeGridRow(int indexY) {
		this.gridBoxes.remove(indexY);
	}

	@Override
	public boolean noHit(FigureMotion<BoxMotion> figure, int offsetX, int offsetY) {
		return figure.getBoxes().stream().allMatch(b -> {
			int x = (int) Math.round(b.getX() / BoxMotion.SIZE);
			int y = (int) Math.round(b.getY() / BoxMotion.SIZE);
			return this.gridBoxes.get(y + offsetY)[x + offsetX] == false;
		});
	}

	@Override
	public void moveDownGrid(int indexY) {
		List<Integer> keys = this.gridBoxes.keySet().stream().filter(y -> y < indexY).collect(Collectors.toList());
		Collections.sort(keys, Collections.reverseOrder());
		keys.stream().forEach(y -> {
			this.gridBoxes.put(y + 1, this.gridBoxes.get(y));
		});
		this.gridBoxes.put(0, new boolean[EngineServiceImpl.WIDTH]);
	}

}
