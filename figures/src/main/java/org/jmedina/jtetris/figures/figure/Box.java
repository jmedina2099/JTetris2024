package org.jmedina.jtetris.figures.figure;

import java.awt.geom.Rectangle2D;

import org.apache.commons.lang3.tuple.Pair;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author Jorge Medina
 *
 */
@NoArgsConstructor
@JsonPropertyOrder({ "x", "y", "initialTimeStamp", "timeStamp" })
@EqualsAndHashCode
public class Box {

	private static final double WIDTH = 20d;
	private static final double HEIGHT = 20d;

	@EqualsAndHashCode.Exclude
	private final Rectangle2D.Double rectangle = new Rectangle2D.Double(0, 0, WIDTH, HEIGHT);

	public long initialTimeStamp;
	public long timeStamp;

	public Box(Pair<Integer, Integer> tupla, long initialTimeStamp, long timeStamp) {
		this.rectangle.x = tupla.getLeft() * WIDTH;
		this.rectangle.y = tupla.getRight() * HEIGHT;
		this.initialTimeStamp = initialTimeStamp;
		this.timeStamp = timeStamp;
	}

	@EqualsAndHashCode.Include
	public double getX() {
		return this.rectangle.x;
	}

	public void setX(double x) {
		this.rectangle.x = x;
	}

	@EqualsAndHashCode.Include
	public double getY() {
		return this.rectangle.y;
	}

	public void setY(double y) {
		this.rectangle.y = y;
	}

}