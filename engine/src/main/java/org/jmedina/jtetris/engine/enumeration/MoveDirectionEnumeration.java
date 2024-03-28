package org.jmedina.jtetris.engine.enumeration;

import lombok.Getter;

/**
 * @author Jorge Medina
 *
 */
@Getter
public enum MoveDirectionEnumeration {

	LEFT(-1), RIGHT(1);

	private int offset;

	MoveDirectionEnumeration(int offset) {
		this.offset = offset;
	}
}
