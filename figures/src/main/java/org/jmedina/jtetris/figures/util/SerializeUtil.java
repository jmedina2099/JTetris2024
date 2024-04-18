package org.jmedina.jtetris.figures.util;

import org.jmedina.jtetris.common.model.FigureOperation;
import org.jmedina.jtetris.figures.exception.ServiceException;
import org.jmedina.jtetris.figures.figure.BoxDB;
import org.jmedina.jtetris.figures.figure.FigureDB;
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

	public String convertFigureOperationToString(FigureOperation<BoxDB,FigureDB<BoxDB>> figureOperation) throws ServiceException {
		try {
			return this.mapper.writeValueAsString(figureOperation);
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}
}