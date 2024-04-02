package org.jmedina.jtetris.engine.kafka.listener;

import java.util.Objects;
import java.util.Optional;

import org.jmedina.jtetris.engine.model.FigureOperation;
import org.jmedina.jtetris.engine.service.EngineService;
import org.jmedina.jtetris.engine.util.SerializeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import lombok.RequiredArgsConstructor;

/**
 * @author Jorge Medina
 *
 */
@RequiredArgsConstructor
@Component
@ConditionalOnProperty(name = "use.kafka", havingValue = "true")
public class MessageListener {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private final SerializeUtil serializeUtil;
	private final EngineService engineService;

	@KafkaListener(topics = "${engine.topic.nextFigure}", groupId = "${engine.groupId.message}")
	public void listenGroupFigureMessage(String message) {
		this.logger.debug("==> MessageListener.listenGroupFigureMessage() = {} ", message);
		Optional<FigureOperation> optional = this.serializeUtil.convertStringToFigureOperation(message);
		if (optional.isPresent() && Objects.nonNull(optional.get().getFigure())
				&& !ObjectUtils.isEmpty(optional.get().getFigure().getBoxes())) {
			this.engineService.addFigureOperation(optional.get());
		}
	}
}
