package org.jmedina.jtetris.figures.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.jmedina.jtetris.figures.exception.ServiceException;
import org.jmedina.jtetris.figures.service.FigureService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * @author Jorge Medina
 *
 */
@WebFluxTest(FigureController.class)
class FigureControllerExceptionIT {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@MockBean
	private FigureService figureService;

	@Autowired
	private WebTestClient client;

	@Test
	@DisplayName("Test for askForNextFigure endpoint with Exception")
	void testGetAskForNextFigure() throws Exception {
		this.logger.debug("==> FigureServiceTest.testGetAskForNextFigure()");
		Mockito.when(this.figureService.askForNextFigure()).thenThrow(new ServiceException(new Exception("TEST")));
		EntityExchangeResult<String> response = this.client.get().uri("/askForNextFigure")
				.header(HttpHeaders.ACCEPT, "application/json").accept(MediaType.APPLICATION_JSON).exchange()
				.expectStatus().is5xxServerError().expectBody(String.class).returnResult();
		assertEquals("{\"message\":\"An error occurred\"}", response.getResponseBody());
	}

}