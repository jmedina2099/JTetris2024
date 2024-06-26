package org.jmedina.jtetris.api.util;

import java.io.IOException;

import org.jmedina.jtetris.common.enumeration.FigureOperationEnumeration;
import org.jmedina.jtetris.common.model.BoxDTO;
import org.jmedina.jtetris.common.model.FigureDTO;
import org.jmedina.jtetris.common.model.FigureOperation;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

/**
 * @author Jorge Medina
 *
 */
public class FigureDeserializer extends StdDeserializer<FigureOperation<BoxDTO,FigureDTO<BoxDTO>>> {

	private static final long serialVersionUID = 680931338299672474L;
	private final ObjectMapper mapper = new ObjectMapper();

	public FigureDeserializer() {
		this(null);
	}

	public FigureDeserializer(Class<?> vc) {
		super(vc);
		this.mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
	}

	@Override
	public FigureOperation<BoxDTO,FigureDTO<BoxDTO>> deserialize(JsonParser jp, DeserializationContext ctxt)
			throws IOException, JacksonException {
		JsonNode node = jp.getCodec().readTree(jp);
		FigureOperationEnumeration operation = FigureOperationEnumeration.valueOf(node.get("operation").asText());
		JsonNode figuresNode = node.get("figure");
		TypeReference<FigureDTO<BoxDTO>> ref = new TypeReference<FigureDTO<BoxDTO>>() {};
		FigureDTO<BoxDTO> figure = this.mapper.readValue(figuresNode.toString(), ref);
		long initialTimeStamp = node.get("initialTimeStamp").asLong();
		long timeStamp = node.get("timeStamp").asLong();
		return FigureOperation.<BoxDTO,FigureDTO<BoxDTO>>builder().operation(operation).figure(figure)
				.initialTimeStamp(initialTimeStamp).timeStamp(timeStamp).build();
	}

}
