package org.jmedina.jtetris.engine.util;

import java.util.List;
import java.util.Optional;

import org.jmedina.jtetris.engine.figure.Figure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Jorge Medina
 *
 */
@Service
public class SerializeUtil {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private final ObjectMapper mapper = new ObjectMapper();

	public Optional<Figure> convertStringToFigure(String message) {
		try {
			return Optional.of(this.mapper.readValue(message, Figure.class));
		} catch (JsonProcessingException e) {
			this.logger.error("==> Error trying to convert JSON to figure!!!", e);
		}
		return Optional.empty();
	}

	public Optional<String> convertFigureToString(Figure figure) {
		try {
			return Optional.of(this.mapper.writeValueAsString(figure));
		} catch (JsonProcessingException e) {
			this.logger.error("==> Error trying to convert figure to JSON!!!", e);
		}
		return Optional.empty();
	}

	public Optional<String> convertListOfFiguresToString(List<Figure> listFigures) {
		try {
			return Optional.of(this.mapper.writeValueAsString(listFigures));
		} catch (JsonProcessingException e) {
			this.logger.error("==> Error trying to convert figure to JSON!!!", e);
		}
		return Optional.empty();
	}

}