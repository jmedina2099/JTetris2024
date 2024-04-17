package org.jmedina.jtetris.engine.figure;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jmedina.jtetris.common.model.Figure;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author Jorge Medina
 *
 */
@NoArgsConstructor
@Data
@ToString
public class FigureMotion implements Figure, Cloneable {

	@JsonIgnore
	@ToString.Exclude
	private final Logger logger = LogManager.getLogger(this.getClass());

	private List<BoxForEngine> boxes = new ArrayList<>();
	public Point center;
	public int numRotations;

	@JsonIgnore
	public int rotation = 0;

	public FigureMotion(List<BoxForEngine> boxes, Point center, int numRotations, int rotation) {
		this.boxes = boxes;
		this.center = center;
		this.numRotations = numRotations;
		this.rotation = rotation;
	}

	public boolean moveRight() {
		this.logger.debug("==> moveRight = {}", boxes);
		this.center.x += BoxForEngine.SIZE;
		return this.boxes.stream().allMatch(BoxForEngine::moveRight);
	}

	public boolean moveLeft() {
		this.logger.debug("==> moveLeft = {}", boxes);
		this.center.x -= BoxForEngine.SIZE;
		return this.boxes.stream().allMatch(BoxForEngine::moveLeft);
	}

	public boolean moveDown() {
		this.logger.debug("==> moveDown = {}", boxes);
		this.center.y += BoxForEngine.SIZE;
		return this.boxes.stream().allMatch(BoxForEngine::moveDown);
	}

	@Override
	public FigureMotion clone() {
		List<BoxForEngine> list = this.boxes.stream().map(b -> b.clone()).collect(Collectors.toList());
		Point center = new Point(this.center.x, this.center.y);
		return new FigureMotion(list, center, numRotations, rotation);
	}

}