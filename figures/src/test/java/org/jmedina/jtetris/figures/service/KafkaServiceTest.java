package org.jmedina.jtetris.figures.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jmedina.jtetris.figures.figure.Caja;
import org.jmedina.jtetris.figures.service.impl.KafkaServiceImpl;
import org.jmedina.jtetris.figures.util.AssertUtilTesting;
import org.jmedina.jtetris.figures.util.SerializeUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
class KafkaServiceTest {

	private final Logger logger = LogManager.getLogger(this.getClass());

	@Value("${figures.topic.nextFigure}")
	public String nextFigureTopic;

	@Autowired
	private SerializeUtil serializeUtil;

	@Autowired
	private KafkaServiceImpl kafkaService;

	@Autowired
	private AssertUtilTesting assertUtil;

	@BeforeEach
	void resetState() {
		this.assertUtil.awaitOneSecond();
		this.assertUtil.resetMessageListener();
	}

	@Test
	@Order(1)
	@DisplayName("Test for sendMessage to kafka")
	void testSendMessage() throws Exception {
		this.logger.debug("==> KafkaServiceTest.testSendMessage()");
		this.kafkaService.sendMessage(this.serializeUtil.convertFigureToString(new Caja()), nextFigureTopic);
		this.assertUtil.assertMessageListenerLatch();
		this.assertUtil.assertMessageCaja();
	}

}