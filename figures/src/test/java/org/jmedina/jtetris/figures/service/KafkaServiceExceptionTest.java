package org.jmedina.jtetris.figures.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.concurrent.CompletableFuture;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jmedina.jtetris.figures.exception.ServiceException;
import org.jmedina.jtetris.figures.helper.KafkaHelperTesting;
import org.jmedina.jtetris.figures.model.FigureOperation;
import org.jmedina.jtetris.figures.service.impl.KafkaServiceImpl;
import org.jmedina.jtetris.figures.util.AssertUtilTesting;
import org.jmedina.jtetris.figures.util.SerializeUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;
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
@TestInstance(Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation.class)
class KafkaServiceExceptionTest extends KafkaHelperTesting {

	private final Logger logger = LogManager.getLogger(this.getClass());

	@Value("${figures.topic.nextFigure}")
	public String nextFigureTopic;

	@MockBean
	private KafkaTemplate<String, String> kafkaTemplate;

	@Autowired
	private SerializeUtil serializeUtil;

	@Autowired
	private KafkaServiceImpl kafkaService;

	@Override
	@BeforeEach
	protected void resetState() {
		super.resetState();
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
		this.kafkaService.sendMessage(this.serializeUtil.convertFigureOperationToString(new FigureOperation()), nextFigureTopic);
		AssertUtilTesting.assertLatch(this.kafkaService.getLatchForTesting());
	}

	@Test
	@Order(2)
	@DisplayName("Test for sendMessage to kafka with Exception")
	void testSendMessageWithException2() throws Exception {
		this.logger.debug("==> KafkaServiceExceptionTest.testSendMessageWithException2()");
		when(this.kafkaTemplate.send(anyString(), anyString())).thenThrow(new ServiceException(new Exception("ERROR")));
		String jsonCaja = this.serializeUtil.convertFigureOperationToString(new FigureOperation());
		ServiceException exception = assertThrows(ServiceException.class, () -> {
			this.kafkaService.sendMessage(jsonCaja, nextFigureTopic);
		});
		assertEquals("org.jmedina.jtetris.figures.exception.ServiceException: java.lang.Exception: ERROR",
				exception.getMessage());
	}

}