package org.jmedina.jtetris.figures.service;

import org.jmedina.jtetris.figures.helper.KafkaHelperTesting;
import org.jmedina.jtetris.figures.service.impl.FigureServiceImpl;
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
import org.springframework.kafka.test.context.EmbeddedKafka;

/**
 * @author Jorge Medina
 *
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestInstance(Lifecycle.PER_CLASS)
@EmbeddedKafka(partitions = 1, brokerProperties = { "listeners=PLAINTEXT://localhost:9092", "port=9092" })
@TestMethodOrder(OrderAnnotation.class)
class FigureServiceDefaultTest extends KafkaHelperTesting {

	@Autowired
	private FigureServiceImpl figureService;

	@Test
	@Order(1)
	@DisplayName("Test for asking to send the next figure")
	void testAskForNextFigure() throws Exception {
		this.logger.debug("==> FigureServiceDefaultTest.testAskForNextFigure()");
		this.figureService.askForNextFigureOperation();
		super.assertMessageListenerLatch();
		super.assertMessageFigure();
	}

}