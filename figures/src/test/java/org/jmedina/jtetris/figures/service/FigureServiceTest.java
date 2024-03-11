package org.jmedina.jtetris.figures.service;

import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jmedina.jtetris.figures.exception.ServiceException;
import org.jmedina.jtetris.figures.helper.KafkaHelperTesting;
import org.jmedina.jtetris.figures.service.impl.FigureServiceImpl;
import org.jmedina.jtetris.figures.util.RandomUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;

/**
 * @author Jorge Medina
 *
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestInstance(Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation.class)
class FigureServiceTest extends KafkaHelperTesting {

	private final Logger logger = LogManager.getLogger(this.getClass());

	@MockBean
	private RandomUtil randomUtil;

	@Autowired
	private FigureServiceImpl figureService;

	@Test
	@Order(1)
	@DisplayName("Test for asking to send the next figure (Caja)")
	void testAskForNextFigureCaja() throws Exception {
		this.logger.debug("==> testAskForNextFigureCaja()");
		when(this.randomUtil.nextInt(2)).thenReturn(0);
		this.figureService.askForNextFigure();
		super.assertMessageListenerLatch();
		super.assertMessageCaja();
	}

	@Test
	@Order(2)
	@DisplayName("Test for asking to send the next figure (Ele)")
	void testAskForNextFigureEle() throws Exception {
		this.logger.debug("==> FigureServiceTest.testAskForNextFigureEle()");
		when(this.randomUtil.nextInt(2)).thenReturn(1);
		this.figureService.askForNextFigure();
		super.assertMessageListenerLatch();
		super.assertMessageEle();
	}

	@Test
	@Order(3)
	@DisplayName("Test for asking to send the next figure with Exception")
	void testAskForNextFigureException() throws Exception {
		this.logger.debug("==> FigureServiceTest.testAskForNextFigureException()");
		when(this.randomUtil.nextInt(2)).thenReturn(-1);
		ServiceException exception = assertThrows(ServiceException.class, () -> {
			this.figureService.askForNextFigure();
		});
		assertEquals("java.lang.IllegalArgumentException: Unexpected value: -1", exception.getMessage());
	}

	@Test
	@Order(4)
	@DisplayName("Test for loadFigurasFromDB")
	void testLoadFigurasFromDB() throws Exception {
		this.logger.debug("==> FigureServiceTest.testLoadFigurasFromDB()");
		this.figureService.loadFigurasFromDB();
		assertTrue(true);
	}
}