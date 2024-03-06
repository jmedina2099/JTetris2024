package org.jmedina.jtetris.figures.util;

import org.jmedina.jtetris.figures.exception.ServiceException;
import org.jmedina.jtetris.figures.figure.Figure;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

/**
 * @author Jorge Medina
 *
 */
@RequiredArgsConstructor
@Service
public class SerializeUtil {

	private final ObjectMapper mapper;

	public <T> T convertStringToFigure(String message, Class<T> clazz) throws ServiceException {
		try {
			return this.mapper.readValue(message, clazz);
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}

	public String convertFigureToString(Figure figure) throws ServiceException {
		try {
			return this.mapper.writeValueAsString(figure);
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}
}