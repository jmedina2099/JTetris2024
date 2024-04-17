package org.jmedina.jtetris.common.model;

import org.jmedina.jtetris.common.enumeration.FigureOperationEnumeration;

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
