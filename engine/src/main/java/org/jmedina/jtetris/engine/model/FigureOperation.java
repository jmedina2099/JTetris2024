package org.jmedina.jtetris.engine.model;

import org.jmedina.jtetris.engine.enumeration.FigureOperationEnumeration;
import org.jmedina.jtetris.engine.figure.Figure;

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
