package org.jmedina.jtetris.figures.service;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.CompletableFuture;

import org.jmedina.jtetris.figures.figure.Caja;
import org.jmedina.jtetris.figures.service.impl.KafkaServiceImpl;
import org.jmedina.jtetris.figures.util.SerializeUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

/**
 * @author Jorge Medina
 *
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class KafkaServiceExceptionTest {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@MockBean
	private KafkaTemplate<String, String> kafkaTemplate;

	@Autowired
	private KafkaServiceImpl kafkaService;

	@Autowired
	private SerializeUtil serializeUtil;

	@Value("${figures.topic.nextFigure}")
	public String nextFigureTopic;

	@Test
	@DisplayName("Test for sendMessage to kafka with Exception")
	void testSendMessageWithException() throws Exception {
		this.logger.debug("==> KafkaServiceExceptionTest.testSendMessageWithException()");
		String message = this.serializeUtil.convertFigureToString(new Caja());
		CompletableFuture<SendResult<String, String>> future = CompletableFuture.supplyAsync(() -> {
			throw new NullPointerException();
		});
		Mockito.when(this.kafkaTemplate.send(Mockito.anyString(), Mockito.anyString())).thenReturn(future);
		this.kafkaService.sendMessage(message, nextFigureTopic);
		assertTrue(true);
	}

}