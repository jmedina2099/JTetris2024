package org.jmedina.jtetris.figures.util;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.jmedina.jtetris.figures.figure.Caja;
import org.junit.jupiter.api.DisplayName;
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
	@DisplayName("Test for convertStringToFigure")
	void testConvertStringToFigure() throws Exception {
		String json = "{\"boxes\":[{\"y\":0.0,\"x\":0.0},{\"y\":20.0,\"x\":0.0},{\"y\":0.0,\"x\":20.0},{\"y\":20.0,\"x\":20.0}]}";
		assertTrue(Objects.nonNull(this.serializeUtil.convertStringToFigure(json, Caja.class)));
	}

	@Test
	@DisplayName("Test for convertFigureToString")
	void testConvertFigureToString() throws Exception {
		assertTrue(StringUtils.isNoneEmpty(this.serializeUtil.convertFigureToString(new Caja())));
	}
}