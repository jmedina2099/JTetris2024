package org.jmedina.jtetris.figures.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.jmedina.jtetris.figures.exception.ServiceException;
import org.jmedina.jtetris.figures.service.impl.FigureServiceImpl;
import org.jmedina.jtetris.figures.util.RandomUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;

/**
 * @author Jorge Medina
 *
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class FigureServiceTest {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@MockBean
	private RandomUtil randomUtil;

	@Autowired
	private FigureServiceImpl figureService;

	@Test
	@DisplayName("Test for asking to send the next figure (Caja)")
	void testAskForNextFigureCaja() throws Exception {
		this.logger.debug("==> FigureServiceTest.testAskForNextFigureCaja()");
		Mockito.when(this.randomUtil.nextInt(2)).thenReturn(0);
		assertTrue(this.figureService.askForNextFigure());
	}

	@Test
	@DisplayName("Test for asking to send the next figure (Ele)")
	void testAskForNextFigureEle() throws Exception {
		this.logger.debug("==> FigureServiceTest.testAskForNextFigureEle()");
		Mockito.when(this.randomUtil.nextInt(2)).thenReturn(1);
		assertTrue(this.figureService.askForNextFigure());
	}

	@Test
	@DisplayName("Test for asking to send the next figure with Exception")
	void testAskForNextFigureException() throws Exception {
		this.logger.debug("==> FigureServiceTest.testAskForNextFigureException()");
		Mockito.when(this.randomUtil.nextInt(2)).thenReturn(-1);
		ServiceException exception = assertThrows(ServiceException.class, () -> {
			this.figureService.askForNextFigure();
		});
		assertEquals("java.lang.IllegalArgumentException: Unexpected value: -1", exception.getMessage());
	}
}
