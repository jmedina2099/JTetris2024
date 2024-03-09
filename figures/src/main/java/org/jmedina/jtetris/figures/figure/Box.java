package org.jmedina.jtetris.figures.figure;

import java.awt.geom.Rectangle2D;

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
public class Box {

	private static final double WIDTH = 20d;
	private static final double HEIGHT = 20d;

	@EqualsAndHashCode.Exclude
	private final Rectangle2D.Double rectangle = new Rectangle2D.Double(0, 0, WIDTH, HEIGHT);

	public Box(int x, int y) {
		this.rectangle.x = x * WIDTH;
		this.rectangle.y = y * HEIGHT;
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