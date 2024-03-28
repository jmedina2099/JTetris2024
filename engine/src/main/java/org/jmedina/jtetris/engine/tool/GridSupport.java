package org.jmedina.jtetris.engine.tool;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.jmedina.jtetris.engine.figure.Box;
import org.jmedina.jtetris.engine.figure.Figure;
import org.springframework.stereotype.Service;

/**
 * @author Jorge Medina
 *
 */
@Service
public class GridSupport {

	private Map<Integer, boolean[]> gridBoxes = new HashMap<>();

	public GridSupport() {
		initializeGrid();
	}

	public Stream<Boolean> getStreamRow(int indexY) {
		boolean[] row = this.gridBoxes.get(indexY);
		return IntStream.range(0, row.length).mapToObj(idx -> row[idx]);
	}

	public void removeGridRow(int indexY) {
		this.gridBoxes.remove(indexY);
	}

	public boolean noHit(Figure figure, int offsetX, int offsetY) {
		return figure.getBoxes().stream().allMatch(b -> {
			int x = (int) Math.round(b.getX() / Box.SIZE);
			int y = (int) Math.round(b.getY() / Box.SIZE);
			return this.gridBoxes.get(y + offsetY)[x + offsetX] == false;
		});
	}

	public void moveDownGrid(int indexY) {
		List<Integer> keys = this.gridBoxes.keySet().stream().filter(y -> y < indexY).collect(Collectors.toList());
		Collections.sort(keys, Collections.reverseOrder());
		keys.stream().forEach(y -> {
			this.gridBoxes.put(y + 1, this.gridBoxes.get(y));
		});
		this.gridBoxes.put(0, new boolean[Engine.WIDTH]);
	}

	public void addToGrid(Figure figure) {
		figure.getBoxes().stream().forEach(b -> {
			int x = (int) Math.round(b.getX() / Box.SIZE);
			int y = (int) Math.round(b.getY() / Box.SIZE);
			this.gridBoxes.get(y)[x] = true;
		});
	}

	public void removeFromGrid(Figure figure) {
		figure.getBoxes().stream().forEach(b -> {
			int x = (int) Math.round(b.getX() / Box.SIZE);
			int y = (int) Math.round(b.getY() / Box.SIZE);
			this.gridBoxes.get(y)[x] = false;
		});
	}

	private void initializeGrid() {
		Stream.iterate(0, x -> x + 1).limit(Engine.HEIGHT).forEach(i -> {
			this.gridBoxes.put(i, new boolean[Engine.WIDTH]);
		});
	}

}
