package org.jmedina.jtetris.common.model;

import org.jmedina.jtetris.common.enumeration.FigureOperationEnumeration;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author Jorge Medina
 *
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@ToString
public class FigureOperation<T extends Figure> {

	private FigureOperationEnumeration operation;
	private T figure;
	private long initialTimeStamp;
	private long timeStamp;
}
