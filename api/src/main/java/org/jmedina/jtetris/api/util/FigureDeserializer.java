package org.jmedina.jtetris.api.util;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

import org.jmedina.jtetris.common.enumeration.FigureOperationEnumeration;
import org.jmedina.jtetris.common.model.FigureDTO;
import org.jmedina.jtetris.common.model.FigureOperation;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

/**
 * @author Jorge Medina
 *
 */
public class FigureDeserializer extends StdDeserializer<FigureOperation> {

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
	public FigureOperation deserialize(JsonParser jp, DeserializationContext ctxt)
			throws IOException, JacksonException {
		JsonNode node = jp.getCodec().readTree(jp);
		FigureOperationEnumeration operation = FigureOperationEnumeration.valueOf(node.get("operation").asText());
		JsonNode figuresNode = node.get("figure");
		FigureDTO figure = this.mapper.readValue(figuresNode.toString(), FigureDTO.class);
		long initialTimeStamp = node.get("initialTimeStamp").asLong();
		long timeStamp = node.get("timeStamp").asLong();
		AtomicReference<FigureDTO> ref = new AtomicReference<>(figure);
		return FigureOperation.builder().operation(operation).figure(ref).initialTimeStamp(initialTimeStamp)
				.timeStamp(timeStamp).build();
	}

}
