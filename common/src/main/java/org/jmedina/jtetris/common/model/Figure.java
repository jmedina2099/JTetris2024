package org.jmedina.jtetris.common.model;

import java.util.List;

/**
 * @author Jorge Medina
 *
 */
public interface Figure {

	public List<? extends Box> getBoxes();

	public String toString();
}
