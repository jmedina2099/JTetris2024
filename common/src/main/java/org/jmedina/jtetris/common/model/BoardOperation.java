package org.jmedina.jtetris.common.model;

import java.util.List;

import org.jmedina.jtetris.common.enumeration.BoardOperationEnumeration;

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
public class BoardOperation {

	private BoardOperationEnumeration operation;
	private List<? extends Box> boxes;
	private long timeStamp;
}
