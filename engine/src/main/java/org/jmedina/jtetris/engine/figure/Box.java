package org.jmedina.jtetris.engine.figure;

import java.awt.geom.Rectangle2D;

import lombok.NoArgsConstructor;

/**
 * @author Jorge Medina
 *
 */
@NoArgsConstructor
public class Box {

	public static final double SIZE = 20d;

	private final Rectangle2D.Double rectangle = new Rectangle2D.Double();

	public boolean moveRight() {
		this.rectangle.x += SIZE;
		return true;
	}

	public boolean moveLeft() {
		this.rectangle.x -= SIZE;
		return true;
	}

	public boolean moveDown() {
		this.rectangle.y += SIZE;
		return true;
	}

	public double getX() {
		return this.rectangle.x;
	}

	public void setX(double x) {
		this.rectangle.x = x;
		this.rectangle.width = SIZE;
	}

	public double getY() {
		return this.rectangle.y;
	}

	public void setY(double y) {
		this.rectangle.y = y;
		this.rectangle.height = SIZE;
	}

	@Override
	public String toString() {
		return getX() + "," + getY();
	}
}