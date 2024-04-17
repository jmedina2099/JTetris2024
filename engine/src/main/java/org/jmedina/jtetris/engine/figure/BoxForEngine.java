package org.jmedina.jtetris.engine.figure;

import java.awt.geom.Rectangle2D;

import org.jmedina.jtetris.common.model.Box;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.NoArgsConstructor;

/**
 * @author Jorge Medina
 *
 */
@NoArgsConstructor
@JsonPropertyOrder({ "x", "y", "initialTimeStamp", "timeStamp" })
public class BoxForEngine implements Box, Cloneable {

	public static final double SIZE = 20d;

	private final Rectangle2D.Double rectangle = new Rectangle2D.Double();

	public BoxForEngine(double x, double y) {
		this.rectangle.x = x;
		this.rectangle.y = y;
	}

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
	public BoxForEngine clone() {
		return new BoxForEngine(this.rectangle.x, this.rectangle.y);
	}

	@Override
	public String toString() {
		return "(" + getX() + "," + getY() + ")";
	}
}