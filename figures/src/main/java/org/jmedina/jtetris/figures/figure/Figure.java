package org.jmedina.jtetris.figures.figure;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.jmedina.jtetris.figures.enumeration.FiguraEnumeration;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Jorge Medina
 *
 */
@Getter
@Setter
@EqualsAndHashCode
public abstract class Figure {

	@JsonIgnore
	@EqualsAndHashCode.Exclude
	protected FiguraEnumeration type;

	@JsonIgnore
	protected int id;
	protected List<Box> boxes;
	protected Point2D.Double center = new Point2D.Double();
	public int numRotations;
	private long initialTimeStamp;
	private long timeStamp;

	protected void init(FiguraEnumeration type) {
		this.type = type;
		this.id = type.getId();
		this.numRotations = type.getNumRotations();
		this.initialTimeStamp = System.nanoTime();
		this.timeStamp = this.initialTimeStamp;
		this.boxes = new ArrayList<>(this.type.getTuplas().stream().map(t -> {
			return new Box(t, this.initialTimeStamp, this.timeStamp);
		}).collect(Collectors.toList()));
		this.center = type.getCenter();
	}
}