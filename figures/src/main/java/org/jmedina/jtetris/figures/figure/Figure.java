package org.jmedina.jtetris.figures.figure;

import java.util.ArrayList;
import java.util.List;

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
	protected int id;
	protected List<Box> boxes;

	@JsonIgnore
	@EqualsAndHashCode.Exclude
	protected FiguraEnumeration type;

	protected void init(FiguraEnumeration type) {
		this.id = type.getId();
		this.boxes = new ArrayList<>();
		this.type = type;
		this.type.getTuplas().stream().forEach(t -> this.boxes.add(new Box(t.getLeft(), t.getRight())));
	}
}