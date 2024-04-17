package org.jmedina.jtetris.engine.util;

import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jmedina.jtetris.common.model.BoardOperation;
import org.jmedina.jtetris.common.model.FigureOperation;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

/**
 * @author Jorge Medina
 *
 */
@Service
public class SerializeUtil {

	private final Logger logger = LogManager.getLogger(this.getClass());
	private final ObjectMapper mapper = new ObjectMapper();
	private final SimpleModule module = new SimpleModule();

	public Optional<FigureOperation> convertStringToFigureOperation(String message) {
		try {
			module.addDeserializer(FigureOperation.class, new FigureDeserializer());
			mapper.registerModule(module);
			return Optional.of(this.mapper.readValue(message, FigureOperation.class));
		} catch (JsonProcessingException e) {
			this.logger.error("==> Error trying to convert JSON to figure operation!!!", e);
		}
		return Optional.empty();
	}

	public Optional<String> convertFigureOperationToString(FigureOperation figureOperation) {
		try {
			return Optional.of(this.mapper.writeValueAsString(figureOperation));
		} catch (JsonProcessingException e) {
			this.logger.error("==> Error trying to convert figure operation to JSON!!!", e);
		}
		return Optional.empty();
	}

	public Optional<String> convertBoardToString(BoardOperation board) {
		try {
			return Optional.of(this.mapper.writeValueAsString(board));
		} catch (JsonProcessingException e) {
			this.logger.error("==> Error trying to convert board operation to JSON!!!", e);
		}
		return Optional.empty();
	}

}