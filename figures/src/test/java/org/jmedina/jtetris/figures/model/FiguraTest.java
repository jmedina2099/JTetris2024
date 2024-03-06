package org.jmedina.jtetris.figures.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * @author Jorge Medina
 *
 */
class FiguraTest {

	@Test
	@DisplayName("Test for Figura model")
	void testFiguraModel() {
		long id = 1L;
		String name = "Caja";
		String boxes = "(0,0)-(0,1)-(1,0)-(1,1)";
		Figura figura = new Figura();
		figura.setId(id);
		figura.setName(name);
		figura.setBoxes(boxes);
		assertEquals(id, figura.getId());
		assertEquals(name, figura.getName());
		assertEquals(boxes, figura.getBoxes());
	}

}
