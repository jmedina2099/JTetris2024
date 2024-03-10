package org.jmedina.jtetris.figures.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.jmedina.jtetris.figures.figure.Caja;
import org.jmedina.jtetris.figures.figure.Ele;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

/**
 * @author Jorge Medina
 *
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class SerializeUtilTest {

	@Autowired
	private SerializeUtil serializeUtil;

	@Test
	@Order(1)
	@DisplayName("Test for convertStringToFigure (CAJA)")
	void testConvertStringToFigureCaja() throws Exception {
		assertEquals(new Caja(), this.serializeUtil.convertStringToFigure(AssertUtilTesting.JSON_CAJA, Caja.class));
	}

	@Test
	@Order(2)
	@DisplayName("Test for convertFigureToString (CAJA)")
	void testConvertFigureToStringCaja() throws Exception {
		assertEquals(AssertUtilTesting.JSON_CAJA, this.serializeUtil.convertFigureToString(new Caja()));
	}

	@Test
	@Order(3)
	@DisplayName("Test for convertStringToFigure (Ele)")
	void testConvertStringToFigureEle() throws Exception {
		assertEquals(new Ele(), this.serializeUtil.convertStringToFigure(AssertUtilTesting.JSON_ELE, Ele.class));
	}

	@Test
	@Order(4)
	@DisplayName("Test for convertFigureToString (Ele)")
	void testConvertFigureToStringEle() throws Exception {
		assertEquals(AssertUtilTesting.JSON_ELE, this.serializeUtil.convertFigureToString(new Ele()));
	}
}