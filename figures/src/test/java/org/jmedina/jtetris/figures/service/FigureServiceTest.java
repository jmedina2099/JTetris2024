package org.jmedina.jtetris.figures.service;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jmedina.jtetris.figures.exception.ServiceException;
import org.jmedina.jtetris.figures.service.impl.FigureServiceImpl;
import org.jmedina.jtetris.figures.util.AssertUtil;
import org.jmedina.jtetris.figures.util.RandomUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;

/**
 * @author Jorge Medina
 *
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DirtiesContext
@EmbeddedKafka(partitions = 1, brokerProperties = { "listeners=PLAINTEXT://localhost:9092", "port=9092" })
class FigureServiceTest {

	private final Logger logger = LogManager.getLogger(this.getClass());

	@MockBean
	private RandomUtil randomUtil;

	@Autowired
	private FigureServiceImpl figureService;

	@Autowired
	private AssertUtil assertUtil;

	@BeforeEach
	void resetState() {
		this.assertUtil.awaitOneSecond();
		this.assertUtil.resetMessageListener();
	}

	@Test
	@Order(1)
	@DisplayName("Test for asking to send the next figure (Caja)")
	void testAskForNextFigureCaja() throws Exception {
		this.logger.debug("==> testAskForNextFigureCaja()");
		when(this.randomUtil.nextInt(2)).thenReturn(0);
		this.figureService.askForNextFigure();
		this.assertUtil.assertMessageListenerLatch();
		this.assertUtil.assertMessageCaja();
	}

	@Test
	@Order(2)
	@DisplayName("Test for asking to send the next figure (Ele)")
	void testAskForNextFigureEle() throws Exception {
		this.logger.debug("==> FigureServiceTest.testAskForNextFigureEle()");
		when(this.randomUtil.nextInt(2)).thenReturn(1);
		this.figureService.askForNextFigure();
		this.assertUtil.assertMessageListenerLatch();
		this.assertUtil.assertMessageEle();
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
}