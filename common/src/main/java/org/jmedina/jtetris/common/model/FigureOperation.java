package org.jmedina.jtetris.common.model;

import org.jmedina.jtetris.common.enumeration.FigureOperationEnumeration;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author Jorge Medina
 * @param <S>
 *
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@ToString
public class FigureOperation<S extends Box, T extends Figure<S>> {

	private FigureOperationEnumeration operation;
	private T figure;
	private long initialTimeStamp;
	private long timeStamp;
}
