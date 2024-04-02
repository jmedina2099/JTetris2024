package org.jmedina.jtetris.engine.model;

import java.util.List;

import org.jmedina.jtetris.engine.enumeration.BoardOperationEnumeration;
import org.jmedina.jtetris.engine.figure.Box;

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
public class BoardOperation {

	private BoardOperationEnumeration operation;
	private List<Box> boxes;
	private long timeStamp;
}
