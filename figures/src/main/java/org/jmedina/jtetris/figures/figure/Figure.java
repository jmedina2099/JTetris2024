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

	protected final List<Box> boxes = new ArrayList<>();

	protected boolean init(FiguraEnumeration f) {
		f.getTuplas().stream().forEach(t -> this.boxes.add(new Box(t.getLeft(), t.getRight())));
		return true;
	}

}