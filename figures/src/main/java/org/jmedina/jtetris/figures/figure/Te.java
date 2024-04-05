package org.jmedina.jtetris.figures.figure;

import static org.jmedina.jtetris.figures.enumeration.FiguraEnumeration.TE;

import lombok.Builder;

/**
 * @author Jorge Medina
 *
 */
@Builder
public class Te extends Figure {

	public Te() {
		super.init(TE);
	}
}