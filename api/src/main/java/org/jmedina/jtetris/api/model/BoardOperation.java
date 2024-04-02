package org.jmedina.jtetris.api.model;

import java.util.ArrayList;
import java.util.List;

import org.jmedina.jtetris.api.enumeration.BoardOperationEnumeration;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Jorge Medina
 *
 */
@NoArgsConstructor
@Data
public class BoardOperation {

	private BoardOperationEnumeration operation;
	private List<Box> boxes = new ArrayList<>();
	private long timeStamp;

}
