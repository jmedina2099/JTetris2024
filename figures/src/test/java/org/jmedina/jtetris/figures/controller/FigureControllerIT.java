package org.jmedina.jtetris.figures.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.jmedina.jtetris.figures.listener.MessageListener;
import org.jmedina.jtetris.figures.util.AssertUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * @author Jorge Medina
 *
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class FigureControllerIT {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private AssertUtil utilTest;

	@Autowired
	private MessageListener messageListenerTest;

	@Autowired
	private WebTestClient client;

	@Test
	@DisplayName("Test for hello endpoint")
	void testGetHello() throws Exception {
		this.logger.debug("==> FigureServiceTest.testGetHello()");
		EntityExchangeResult<String> response = this.client.get().uri("/hello")
				.header(HttpHeaders.ACCEPT, "application/json").accept(MediaType.APPLICATION_JSON).exchange()
				.expectStatus().isOk().expectBody(String.class).returnResult();
		assertEquals("Hello from figures reactive!!!", response.getResponseBody());
	}

	@Test
	@DisplayName("Test for askForNextFigure endpoint")
	void testGetAskForNextFigure() throws Exception {
		this.logger.debug("==> FigureServiceTest.testGetAskForNextFigure()");
		this.messageListenerTest.setMessage(null);
		EntityExchangeResult<Boolean> response = this.client.get().uri("/askForNextFigure")
				.header(HttpHeaders.ACCEPT, "application/json").accept(MediaType.APPLICATION_JSON).exchange()
				.expectStatus().isOk().expectBody(Boolean.class).returnResult();
		assertEquals(true, response.getResponseBody());
		this.utilTest.assertMessageListener();
	}
}
