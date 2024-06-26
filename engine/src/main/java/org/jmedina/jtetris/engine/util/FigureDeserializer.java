package org.jmedina.jtetris.engine.util;

import java.io.IOException;

import org.jmedina.jtetris.common.enumeration.FigureOperationEnumeration;
import org.jmedina.jtetris.common.model.FigureOperation;
import org.jmedina.jtetris.engine.figure.BoxMotion;
import org.jmedina.jtetris.engine.figure.FigureMotion;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

/**
 * @author Jorge Medina
 *
 */
public class FigureDeserializer extends StdDeserializer<FigureOperation<BoxMotion,FigureMotion<BoxMotion>>> {

	private static final long serialVersionUID = 4200604337919878918L;

	private final ObjectMapper mapper = new ObjectMapper();

	public FigureDeserializer() {
		this(null);
	}

	public FigureDeserializer(Class<?> vc) {
		super(vc);
	}

	@Override
	public FigureOperation<BoxMotion,FigureMotion<BoxMotion>> deserialize(JsonParser jp, DeserializationContext ctxt)
			throws IOException, JacksonException {
		JsonNode node = jp.getCodec().readTree(jp);
		FigureOperationEnumeration operation = FigureOperationEnumeration.valueOf(node.get("operation").asText());
		TypeReference<FigureMotion<BoxMotion>> ref = new TypeReference<FigureMotion<BoxMotion>>() {};
		FigureMotion<BoxMotion> figure = this.mapper.readValue(node.get("figure").toString(), ref);
		long initialTimeStamp = node.get("initialTimeStamp").asLong();
		long timeStamp = node.get("timeStamp").asLong();
		return FigureOperation.<BoxMotion,FigureMotion<BoxMotion>>builder().operation(operation).figure(figure)
				.initialTimeStamp(initialTimeStamp).timeStamp(timeStamp).build();
	}

}
