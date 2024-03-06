package org.jmedina.jtetris.figures.service;

import java.util.concurrent.CompletableFuture;

import org.jmedina.jtetris.figures.figure.Caja;
import org.jmedina.jtetris.figures.listener.MessageListener;
import org.jmedina.jtetris.figures.service.impl.KafkaServiceImpl;
import org.jmedina.jtetris.figures.util.AssertUtil;
import org.jmedina.jtetris.figures.util.SerializeUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

/**
 * @author Jorge Medina
 *
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class KafkaServiceTest {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Mock
	private KafkaTemplate<String, String> kafkaTemplate;

	@InjectMocks
	private KafkaServiceImpl kafkaService;

	@Autowired
	private SerializeUtil serializeUtil;

	@Autowired
	private AssertUtil utilTest;

	@Autowired
	private MessageListener messageListenerTest;

	@Value("${figures.topic.nextFigure}")
	public String nextFigureTopic;

	@Test
	@DisplayName("Test for sendMessage to kafka")
	void testSendMessage() throws Exception {
		this.logger.debug("==> KafkaServiceTest.testSendMessage()");
		Caja figure = new Caja();
		String message = this.serializeUtil.convertFigureToString(figure);
		CompletableFuture<SendResult<String, String>> future = CompletableFuture.supplyAsync(() -> {
			return null;
		});
		Mockito.when(this.kafkaTemplate.send(Mockito.anyString(), Mockito.anyString())).thenReturn(future);
		this.messageListenerTest.setMessage(null);
		this.kafkaService.sendMessage(message, nextFigureTopic);
		this.utilTest.assertFalseMessageListener();
	}

	@Test
	@DisplayName("Test for sendMessage to kafka with Exception")
	void testSendMessageWithException() throws Exception {
		this.logger.debug("==> KafkaServiceTest.testSendMessageWithException()");
		Caja figure = new Caja();
		String message = this.serializeUtil.convertFigureToString(figure);
		CompletableFuture<SendResult<String, String>> future = CompletableFuture.supplyAsync(() -> {
			throw new NullPointerException();
		});
		Mockito.when(this.kafkaTemplate.send(Mockito.anyString(), Mockito.anyString())).thenReturn(future);
		this.messageListenerTest.setMessage(null);
		this.kafkaService.sendMessage(message, nextFigureTopic);
		this.utilTest.assertFalseMessageListener();
	}

}