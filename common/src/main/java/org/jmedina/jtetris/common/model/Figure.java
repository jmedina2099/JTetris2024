package org.jmedina.jtetris.common.model;

import java.util.List;

/**
 * @author Jorge Medina
 *
 */
public interface Figure<T extends Box> {

	public List<T> getBoxes();

	public String toString();
}
