/**
 * 
 */
package org.jmedina.jtetris.figures.service;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.jmedina.jtetris.figures.service.impl.FigureServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

/**
 * @author Jorge Medina
 *
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class FigureServiceDefaultTest {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private FigureServiceImpl figureService;

	@Test
	@DisplayName("Test for asking to send the next figure")
	void testAskForNextFigure() throws Exception {
		this.logger.debug("==> FigureServiceDefaultTest.testAskForNextFigure()");
		assertTrue(this.figureService.askForNextFigure());
	}

}