package org.jmedina.jtetris.engine.figure;

import java.awt.geom.Rectangle2D;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.NoArgsConstructor;

/**
 * @author Jorge Medina
 *
 */
@NoArgsConstructor
@JsonPropertyOrder({ "x", "y", "initialTimeStamp", "timeStamp" })
public class Box implements Cloneable {

	public static final double SIZE = 20d;

	private final Rectangle2D.Double rectangle = new Rectangle2D.Double();
	public long initialTimeStamp;
	public long timeStamp;
	
	public Box(double x, double y, long initialTimeStamp, long timeStamp) {
		this.rectangle.x = x;
		this.rectangle.y = y;
		this.initialTimeStamp = initialTimeStamp;
		this.timeStamp = timeStamp;
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
	protected Box clone() {
		return new Box(this.rectangle.x,this.rectangle.y,this.initialTimeStamp,this.timeStamp);
	}

	@Override
	public String toString() {
		return getX() + "," + getY();
	}
}