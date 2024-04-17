package org.jmedina.jtetris.figures.figure;

import static org.jmedina.jtetris.figures.enumeration.FiguraEnumeration.ELE;

import lombok.Builder;

/**
 * @author Jorge Medina
 *
 */
@Builder
public class Ele extends FigureForFigures {

	public Ele() {
		super.init(ELE);
	}
}