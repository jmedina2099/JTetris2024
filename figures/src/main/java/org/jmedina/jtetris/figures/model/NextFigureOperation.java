package org.jmedina.jtetris.figures.model;

import org.jmedina.jtetris.figures.enumeration.FigureOperationEnumeration;

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
public class NextFigureOperation {

	private FigureOperationEnumeration operation;
}
