package org.jmedina.jtetris.figures.service;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.concurrent.CompletableFuture;

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
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

/**
 * @author Jorge Medina
 *
 */
@SpringBootTest
class KafkaServiceExceptionTest {

	private final Logger logger = LogManager.getLogger(this.getClass());

	@Value("${figures.topic.nextFigure}")
	public String nextFigureTopic;

	@MockBean
	private KafkaTemplate<String, String> kafkaTemplate;

	@Autowired
	private SerializeUtil serializeUtil;

	@Autowired
	private KafkaServiceImpl kafkaService;

	@Autowired
	private AssertUtilTesting assertUtil;

	@BeforeEach
	void resetState() {
		this.assertUtil.awaitOneSecond();
		this.kafkaService.resetLatchForTesting();
	}

	@Test
	@Order(1)
	@DisplayName("Test for sendMessage to kafka with Exception")
	void testSendMessageWithException() throws Exception {
		this.logger.debug("==> KafkaServiceExceptionTest.testSendMessageWithException()");
		CompletableFuture<SendResult<String, String>> future = CompletableFuture.supplyAsync(() -> {
			throw new NullPointerException();
		});
		when(this.kafkaTemplate.send(anyString(), anyString())).thenReturn(future);
		this.kafkaService.sendMessage(this.serializeUtil.convertFigureToString(new Caja()), nextFigureTopic);
		this.assertUtil.assertLatch(this.kafkaService.getLatchForTesting());
	}

}