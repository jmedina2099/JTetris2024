package org.jmedina.jtetris.engine.figure;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Jorge Medina
 *
 */
@NoArgsConstructor
@Data
public class Figure implements Cloneable {

	@JsonIgnore
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private List<Box> boxes = new ArrayList<>();
	public Point center;
	public int numRotations;
	private long initialTimeStamp;
	private long timeStamp;

	@JsonIgnore
	public int rotation = 0;

	public Figure(List<Box> boxes, Point center, int numRotations, int rotation, long initialTimeStamp,
			long timeStamp) {
		this.boxes = boxes;
		this.center = center;
		this.numRotations = numRotations;
		this.rotation = rotation;
		this.initialTimeStamp = initialTimeStamp;
		this.timeStamp = timeStamp;
	}

	public boolean moveRight() {
		this.logger.debug("==> moveRight = {}", boxes);
		this.center.x += Box.SIZE;
		return this.boxes.stream().allMatch(Box::moveRight);
	}

	public boolean moveLeft() {
		this.logger.debug("==> moveLeft = {}", boxes);
		this.center.x -= Box.SIZE;
		return this.boxes.stream().allMatch(Box::moveLeft);
	}

	public boolean moveDown() {
		this.logger.debug("==> moveDown = {}", boxes);
		this.center.y += Box.SIZE;
		return this.boxes.stream().allMatch(Box::moveDown);
	}

	@Override
	public Figure clone() {
		List<Box> list = this.boxes.stream().map(b -> b.clone()).collect(Collectors.toList());
		Point center = new Point(this.center.x, this.center.y);
		return new Figure(list, center, numRotations, rotation, this.initialTimeStamp, this.timeStamp);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		this.boxes.stream().forEach(b -> sb.append(b.toString()));
		return sb.toString();
	}

	public void setTimeStampPropagate(long nanoTime) {
		this.timeStamp = nanoTime;
		this.boxes.stream().forEach(b -> b.timeStamp = nanoTime);

	}

}