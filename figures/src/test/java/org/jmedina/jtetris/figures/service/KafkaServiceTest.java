package org.jmedina.jtetris.figures.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jmedina.jtetris.figures.figure.Caja;
import org.jmedina.jtetris.figures.helper.KafkaHelperTesting;
import org.jmedina.jtetris.figures.service.impl.KafkaServiceImpl;
import org.jmedina.jtetris.figures.util.SerializeUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

/**
 * @author Jorge Medina
 *
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestInstance(Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation.class)
class KafkaServiceTest extends KafkaHelperTesting {

	private final Logger logger = LogManager.getLogger(this.getClass());

	@Value("${figures.topic.nextFigure}")
	public String nextFigureTopic;

	@Autowired
	private SerializeUtil serializeUtil;

	@Autowired
	private KafkaServiceImpl kafkaService;

	@Test
	@Order(1)
	@DisplayName("Test for sendMessage to kafka")
	void testSendMessage() throws Exception {
		this.logger.debug("==> KafkaServiceTest.testSendMessage()");
		this.kafkaService.sendMessage(this.serializeUtil.convertFigureToString(new Caja()), nextFigureTopic);
		super.assertMessageListenerLatch();
		super.assertMessageCaja();
	}

}