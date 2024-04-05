package org.jmedina.jtetris.figures.figure;

import static org.jmedina.jtetris.figures.enumeration.FiguraEnumeration.VERTICAL;

import lombok.Builder;

/**
 * @author Jorge Medina
 *
 */
@Builder
public class Vertical extends Figure {

	public Vertical() {
		super.init(VERTICAL);
	}
}