package org.jmedina.jtetris.figures.figure;

import static org.jmedina.jtetris.figures.enumeration.FiguraEnumeration.CAJA;

import lombok.Builder;

/**
 * @author Jorge Medina
 *
 */
@Builder
public class Caja extends Figure {

	public Caja() {
		super.init(CAJA);
	}
}