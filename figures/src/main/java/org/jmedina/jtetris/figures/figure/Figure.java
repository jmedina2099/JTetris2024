package org.jmedina.jtetris.figures.figure;

import java.util.ArrayList;
import java.util.List;

import org.jmedina.jtetris.figures.enumeration.FiguraEnumeration;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Jorge Medina
 *
 */
@Getter
@Setter
public abstract class Figure {

	protected List<Box> boxes;

	protected boolean init(FiguraEnumeration f) {
		this.boxes = new ArrayList<>();
		f.getTuplas().stream().forEach(t -> this.boxes.add(new Box(t.getLeft(), t.getRight())));
		return true;
	}

}