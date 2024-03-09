package org.jmedina.jtetris.figures.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.ACCEPT;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jmedina.jtetris.figures.util.AssertUtilTesting;
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
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * @author Jorge Medina
 *
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@EmbeddedKafka(partitions = 1, brokerProperties = { "listeners=PLAINTEXT://localhost:9092", "port=9092" })
class FigureControllerIT {

	private final Logger logger = LogManager.getLogger(this.getClass());

	@Autowired
	private WebTestClient client;

	@MockBean
	private RandomUtil random;

	@Autowired
	private AssertUtilTesting assertUtil;

	@BeforeEach
	void resetState() {
		this.assertUtil.awaitOneSecond();
		this.assertUtil.resetMessageListener();
	}

	@Test
	@Order(1)
	@DisplayName("Test for hello endpoint")
	void testGetHello() throws Exception {
		this.logger.debug("==> FigureServiceTest.testGetHello()");
		EntityExchangeResult<String> response = this.client.get().uri("/hello").header(ACCEPT, "application/json")
				.accept(APPLICATION_JSON).exchange().expectStatus().isOk().expectBody(String.class).returnResult();
		assertEquals("Hello from figures reactive!!!", response.getResponseBody());
	}

	@Test
	@Order(2)
	@DisplayName("Test for askForNextFigureCaja endpoint")
	void testGetAskForNextFigureCaja() throws Exception {
		this.logger.debug("==> FigureServiceTest.testGetAskForNextFigureCaja()");
		when(this.random.nextInt(anyInt())).thenReturn(0);
		EntityExchangeResult<Boolean> response = this.client.get().uri("/askForNextFigure")
				.header(ACCEPT, "application/json").accept(APPLICATION_JSON).exchange().expectStatus().isOk()
				.expectBody(Boolean.class).returnResult();
		assertTrue(response.getResponseBody());
		this.assertUtil.assertMessageListenerLatch();
		this.assertUtil.assertMessageCaja();
	}

	@Test
	@Order(3)
	@DisplayName("Test for askForNextFigureEle endpoint")
	void testGetAskForNextFigureEle() throws Exception {
		this.logger.debug("==> FigureServiceTest.testGetAskForNextFigureEle()");
		when(this.random.nextInt(anyInt())).thenReturn(1);
		EntityExchangeResult<Boolean> response = this.client.get().uri("/askForNextFigure")
				.header(ACCEPT, "application/json").accept(APPLICATION_JSON).exchange().expectStatus().isOk()
				.expectBody(Boolean.class).returnResult();
		assertTrue(response.getResponseBody());
		this.assertUtil.assertMessageListenerLatch();
		this.assertUtil.assertMessageEle();
	}
}