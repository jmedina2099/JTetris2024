package org.jmedina.jtetris.api.model;

import org.jmedina.jtetris.api.enumeration.FigureOperationEnumeration;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Jorge Medina
 *
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class FigureOperation {

	private FigureOperationEnumeration operation;
	private Figure figure;
	private long initialTimeStamp;
	private long timeStamp;

}
