package org.jmedina.jtetris.engine.figure;

import java.util.ArrayList;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Jorge Medina
 *
 */
@NoArgsConstructor
@Data
public class Figure {

	private ArrayList<Box> boxes = new ArrayList<>();

	public boolean moveRight() {
		return boxes.stream().allMatch(Box::moveRight);
	}

	public boolean moveLeft() {
		return boxes.stream().allMatch(Box::moveLeft);
	}

	public boolean moveDown() {
		return boxes.stream().allMatch(Box::moveDown);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		this.boxes.stream().forEach(b -> sb.append(b.toString()));
		return sb.toString();
	}

}