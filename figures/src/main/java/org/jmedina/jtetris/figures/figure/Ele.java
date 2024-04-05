package org.jmedina.jtetris.figures.figure;

import static org.jmedina.jtetris.figures.enumeration.FiguraEnumeration.ELE;

import lombok.Builder;

/**
 * @author Jorge Medina
 *
 */
@Builder
public class Ele extends Figure {

	public Ele() {
		super.init(ELE);
	}
}