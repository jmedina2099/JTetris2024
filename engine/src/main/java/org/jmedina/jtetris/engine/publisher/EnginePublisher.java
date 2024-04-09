package org.jmedina.jtetris.engine.publisher;

import org.jmedina.jtetris.engine.model.FigureOperation;
import org.reactivestreams.Subscriber;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * @author Jorge Medina
 *
 */
@Service
public class EnginePublisher extends CustomPublisher<FigureOperation> {

	public EnginePublisher() {
		super(LoggerFactory.getLogger(EnginePublisher.class));
	}

	@Override
	public void subscribe(Subscriber<? super FigureOperation> subscriber) {
		this.logger.debug("===> FigurePublisher.subscribe()");
		super.subscribe(subscriber);
	}

	public void sendFigureOperation(FigureOperation figureOperation) {
		this.logger.debug("===> EnginePublisher.sendFigureOperation() = " + figureOperation);
		try {
			super.addToQueue(figureOperation);
		} catch (Exception e) {
			this.logger.error("=*=> ERROR: ", e);
		}
	}
}
