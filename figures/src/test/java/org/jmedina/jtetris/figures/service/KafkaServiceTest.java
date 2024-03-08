package org.jmedina.jtetris.figures.service;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.jmedina.jtetris.figures.figure.Caja;
import org.jmedina.jtetris.figures.service.impl.KafkaServiceImpl;
import org.jmedina.jtetris.figures.util.SerializeUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

/**
 * @author Jorge Medina
 *
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class KafkaServiceTest {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private KafkaServiceImpl kafkaService;

	@Autowired
	private SerializeUtil serializeUtil;

	@Value("${figures.topic.nextFigure}")
	public String nextFigureTopic;

	@Test
	@DisplayName("Test for sendMessage to kafka")
	void testSendMessage() throws Exception {
		this.logger.debug("==> KafkaServiceTest.testSendMessage()");
		String message = this.serializeUtil.convertFigureToString(new Caja());
		this.kafkaService.sendMessage(message, nextFigureTopic);
		assertTrue(true);
	}

}