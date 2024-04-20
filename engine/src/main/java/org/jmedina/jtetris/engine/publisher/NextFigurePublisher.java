package org.jmedina.jtetris.engine.publisher;

import org.apache.logging.log4j.LogManager;
import org.jmedina.jtetris.common.publisher.CustomPublisher;
import org.jmedina.jtetris.engine.model.NextFigureOperation;
import org.reactivestreams.Subscriber;

/**
 * @author Jorge Medina
 *
 */
public class NextFigurePublisher extends CustomPublisher<NextFigureOperation> {

	public NextFigurePublisher() {
		super(LogManager.getLogger(NextFigurePublisher.class));
	}

	@Override
	public void subscribe(Subscriber<? super NextFigureOperation> subscriber) {
		this.logger.debug("===> NextFigurePublisher.subscribe()");
		super.subscribe(subscriber);
	}

	public void sendNextFigurePetition(NextFigureOperation op) {
		this.logger.debug("===> NextFigurePublisher.sendNextFigurePetition() = " + op);
		try {
			super.addToQueue(op);
		} catch (Exception e) {
			this.logger.error("=*=> ERROR: ", e);
		}
	}
}
