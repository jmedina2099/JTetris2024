package org.jmedina.jtetris.figures.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.jmedina.jtetris.figures.exception.ServiceException;
import org.jmedina.jtetris.figures.figure.Caja;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Jorge Medina
 *
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class SerializeUtilExceptionTest {

	@MockBean
	private ObjectMapper mapper;

	@Autowired
	private SerializeUtil serializeUtil;

	@SuppressWarnings({ "unchecked", "deprecation" })
	@Test
	@DisplayName("Test for convertStringToFigure with Exception")
	void testConvertStringToFigure() throws ServiceException {
		try {
			Mockito.when(mapper.readValue(Mockito.anyString(), Mockito.any(Class.class)))
					.thenThrow(new JsonMappingException("ERROR"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		String cajaJson = "{\"boxes\":[{\"y\":0.0,\"x\":0.0},{\"y\":20.0,\"x\":0.0},{\"y\":0.0,\"x\":20.0},{\"y\":20.0,\"x\":20.0}]}";
		ServiceException exception = assertThrows(ServiceException.class, () -> {
			this.serializeUtil.convertStringToFigure(cajaJson, Caja.class);
		});
		assertEquals("com.fasterxml.jackson.databind.JsonMappingException: ERROR", exception.getMessage());
	}

	@SuppressWarnings({ "deprecation" })
	@Test
	@DisplayName("Test for convertFigureToString with Exception")
	void testConvertFigureToString() throws Exception {
		try {
			Mockito.when(mapper.writeValueAsString(Mockito.any(Object.class)))
					.thenThrow(new JsonMappingException("ERROR"));
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