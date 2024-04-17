package org.jmedina.jtetris.engine.figure;

import java.awt.geom.Point2D;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.NoArgsConstructor;

/**
 * @author Jorge Medina
 *
 */
@NoArgsConstructor
@JsonPropertyOrder({ "x", "y" })
public class Point extends Point2D.Double {

	private static final long serialVersionUID = -5445737127696074062L;

	public Point(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public double getX() {
		return this.x;
	}

	public void setX(double x) {
		this.x = x * BoxForEngine.SIZE;
	}

	public double getY() {
		return this.y;
	}

	public void setY(double y) {
		this.y = y * BoxForEngine.SIZE;
	}

}