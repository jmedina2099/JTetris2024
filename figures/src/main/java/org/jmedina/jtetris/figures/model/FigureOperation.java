package org.jmedina.jtetris.figures.model;

import org.jmedina.jtetris.figures.enumeration.FigureOperationEnumeration;
import org.jmedina.jtetris.figures.figure.Figure;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Jorge Medina
 *
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class FigureOperation {

	private FigureOperationEnumeration operation;
	private Figure figure;
	private long initialTimeStamp;
	private long timeStamp;

}
