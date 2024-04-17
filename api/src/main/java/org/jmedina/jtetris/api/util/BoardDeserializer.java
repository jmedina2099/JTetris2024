package org.jmedina.jtetris.api.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jmedina.jtetris.common.enumeration.BoardOperationEnumeration;
import org.jmedina.jtetris.common.model.BoardOperation;
import org.jmedina.jtetris.common.model.BoxDTO;

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
public class BoardDeserializer extends StdDeserializer<BoardOperation> {

	private static final long serialVersionUID = 680931338299672474L;
	private final ObjectMapper mapper = new ObjectMapper();

	public BoardDeserializer() {
		this(null);
	}

	public BoardDeserializer(Class<?> vc) {
		super(vc);
		this.mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
	}

	@Override
	public BoardOperation deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JacksonException {
		JsonNode node = jp.getCodec().readTree(jp);
		BoardOperationEnumeration operation = BoardOperationEnumeration.valueOf(node.get("operation").asText());
		JsonNode boxesNode = node.get("boxes");
		List<BoxDTO> boxes = new ArrayList<>();
		if (boxesNode.isArray()) {
			for (final JsonNode objNode : boxesNode) {
				BoxDTO box = this.mapper.readValue(objNode.toString(), BoxDTO.class);
				boxes.add(box);
			}
		}
		long timeStamp = node.get("timeStamp").asLong();
		return BoardOperation.builder().operation(operation).boxes(boxes).timeStamp(timeStamp).build();
	}

}
