package org.jmedina.jtetris.figures.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import org.jmedina.jtetris.figures.exception.ServiceException;
import org.jmedina.jtetris.figures.figure.Caja;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Jorge Medina
 *
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DirtiesContext
class SerializeUtilExceptionTest {

	@MockBean
	private ObjectMapper mapper;

	@Autowired
	private SerializeUtil serializeUtil;

	@SuppressWarnings({ "unchecked", "deprecation" })
	@Test
	@Order(1)
	@DisplayName("Test for convertStringToFigure with Exception")
	void testConvertStringToFigure() throws ServiceException {
		try {
			when(mapper.readValue(Mockito.anyString(), Mockito.any(Class.class)))
					.thenThrow(new JsonMappingException("ERROR"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		ServiceException exception = assertThrows(ServiceException.class, () -> {
			this.serializeUtil.convertStringToFigure(AssertUtilTesting.JSON_CAJA, Caja.class);
		});
		assertEquals("com.fasterxml.jackson.databind.JsonMappingException: ERROR", exception.getMessage());
	}

	@SuppressWarnings({ "deprecation" })
	@Test
	@Order(2)
	@DisplayName("Test for convertFigureToString with Exception")
	void testConvertFigureToString() throws Exception {
		try {
			when(mapper.writeValueAsString(Mockito.any(Object.class))).thenThrow(new JsonMappingException("ERROR"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		Caja caja = new Caja();
		ServiceException exception = assertThrows(ServiceException.class, () -> {
			this.serializeUtil.convertFigureToString(caja);
		});
		assertEquals("com.fasterxml.jackson.databind.JsonMappingException: ERROR", exception.getMessage());
	}

}