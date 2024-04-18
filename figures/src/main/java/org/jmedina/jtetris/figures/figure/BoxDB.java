package org.jmedina.jtetris.figures.figure;

import java.awt.geom.Rectangle2D;

import org.apache.commons.lang3.tuple.Pair;
import org.jmedina.jtetris.common.model.Box;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author Jorge Medina
 *
 */
@NoArgsConstructor
@JsonPropertyOrder({ "x", "y" })
@EqualsAndHashCode
public class BoxDB implements Box {

	private static final double WIDTH = 20d;
	private static final double HEIGHT = 20d;

	@EqualsAndHashCode.Exclude
	private final Rectangle2D.Double rectangle = new Rectangle2D.Double(0, 0, WIDTH, HEIGHT);

	public BoxDB(Pair<Integer, Integer> tupla) {
		this.rectangle.x = tupla.getLeft() * WIDTH;
		this.rectangle.y = tupla.getRight() * HEIGHT;
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

	@Override
	public String toString() {
		return "(" + getX() + "," + getY() + ")";
	}

}