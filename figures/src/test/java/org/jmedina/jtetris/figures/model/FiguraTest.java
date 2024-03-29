package org.jmedina.jtetris.figures.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.jmedina.jtetris.figures.domain.Figura;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;

/**
 * @author Jorge Medina
 *
 */
@TestMethodOrder(OrderAnnotation.class)
class FiguraTest {

	@Test
	@Order(1)
	@DisplayName("Test for Figura model")
	void testFiguraModel() {
		String id = "figCaja";
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

	@Test
	@Order(2)
	@DisplayName("Test for Figura Constructor")
	void testFiguraConstructor() {
		String id = "figCaja";
		String name = "Caja";
		String boxes = "(0,0)-(0,1)-(1,0)-(1,1)";
		String center = "0,1";
		int numRotaciones = 4;
		Figura figura = new Figura(id,name,boxes,center,numRotaciones);
		assertEquals(id, figura.getId());
		assertEquals(name, figura.getName());
		assertEquals(boxes, figura.getBoxes());
	}
}