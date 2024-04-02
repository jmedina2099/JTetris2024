package org.jmedina.jtetris.figures.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.springframework.http.HttpHeaders.ACCEPT;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jmedina.jtetris.figures.exception.ServiceException;
import org.jmedina.jtetris.figures.service.FigureService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * @author Jorge Medina
 *
 */
@WebFluxTest(FigureController.class)
@TestMethodOrder(OrderAnnotation.class)
class FigureControllerExceptionIT {

	private final Logger logger = LogManager.getLogger(this.getClass());

	@MockBean
	private FigureService figureService;

	@Autowired
	private WebTestClient client;

	@Test
	@Order(1)
	@DisplayName("Test for askForNextFigure endpoint with Exception")
	void testGetAskForNextFigure() throws Exception {
		this.logger.debug("==> FigureServiceTest.testGetAskForNextFigure()");
		doThrow(new ServiceException(new Exception("TEST"))).when(this.figureService).askForNextFigureOperation();
		EntityExchangeResult<String> response = this.client.get().uri("/askForNextFigure")
				.header(ACCEPT, "application/json").accept(APPLICATION_JSON).exchange().expectStatus()
				.is5xxServerError().expectBody(String.class).returnResult();
		assertEquals("{\"message\":\"An error occurred\"}", response.getResponseBody());
	}

}