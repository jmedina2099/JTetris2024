package org.jmedina.jtetris.engine.publisher;

import org.jmedina.jtetris.engine.model.NextFigureOperation;
import org.reactivestreams.Subscriber;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * @author Jorge Medina
 *
 */
@Service
public class NextFigurePublisher extends CustomPublisher<NextFigureOperation> {

	public NextFigurePublisher() {
		super(LoggerFactory.getLogger(NextFigurePublisher.class));
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
