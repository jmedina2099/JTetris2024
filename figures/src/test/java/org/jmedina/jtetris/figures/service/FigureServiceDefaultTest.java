package org.jmedina.jtetris.figures.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jmedina.jtetris.figures.service.impl.FigureServiceImpl;
import org.jmedina.jtetris.figures.util.AssertUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;

/**
 * @author Jorge Medina
 *
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DirtiesContext
@EmbeddedKafka(partitions = 1, brokerProperties = { "listeners=PLAINTEXT://localhost:9092", "port=9092" })
class FigureServiceDefaultTest {

	private final Logger logger = LogManager.getLogger(this.getClass());

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
	@DisplayName("Test for asking to send the next figure")
	void testAskForNextFigure() throws Exception {
		this.logger.debug("==> FigureServiceDefaultTest.testAskForNextFigure()");
		this.figureService.askForNextFigure();
		this.assertUtil.assertMessageListenerLatch();
		this.assertUtil.assertMessageFigure();
	}

}