package org.jmedina.jtetris.figures.figure;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Objects;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * @author Jorge Medina
 *
 */
class FigureTest {

	@Test
	@DisplayName("Test for Caja")
	void testCaja() {
		Caja caja = new Caja();
		assertTrue(Objects.nonNull(caja));
	}

	@Test
	@DisplayName("Test for Ele")
	void testEle() {
		Ele ele = new Ele();
		assertTrue(Objects.nonNull(ele));
	}
}
