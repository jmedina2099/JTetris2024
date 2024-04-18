package org.jmedina.jtetris.engine.publisher;

import org.apache.logging.log4j.LogManager;
import org.jmedina.jtetris.common.model.FigureOperation;
import org.jmedina.jtetris.common.publisher.CustomPublisher;
import org.jmedina.jtetris.engine.figure.BoxMotion;
import org.jmedina.jtetris.engine.figure.FigureMotion;
import org.reactivestreams.Subscriber;
import org.springframework.stereotype.Service;

/**
 * @author Jorge Medina
 *
 */
@Service
public class EnginePublisher extends CustomPublisher<FigureOperation<BoxMotion,FigureMotion<BoxMotion>>> {

	public EnginePublisher() {
		super(LogManager.getLogger(EnginePublisher.class));
	}

	@Override
	public void subscribe(Subscriber<? super FigureOperation<BoxMotion,FigureMotion<BoxMotion>>> subscriber) {
		this.logger.debug("===> FigurePublisher.subscribe()");
		super.subscribe(subscriber);
	}

	public void sendFigureOperation(FigureOperation<BoxMotion,FigureMotion<BoxMotion>> figureOperation) {
		this.logger.debug("===> EnginePublisher.sendFigureOperation() = " + figureOperation);
		try {
			super.addToQueue(figureOperation);
		} catch (Exception e) {
			this.logger.error("=*=> ERROR: ", e);
		}
	}
}
