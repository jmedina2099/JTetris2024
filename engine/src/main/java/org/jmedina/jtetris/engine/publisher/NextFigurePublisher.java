package org.jmedina.jtetris.engine.publisher;

import org.jmedina.jtetris.engine.model.Message;
import org.reactivestreams.Subscriber;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * @author Jorge Medina
 *
 */
@Service
public class NextFigurePublisher extends CustomPublisher<Message> {

	public NextFigurePublisher() {
		super(LoggerFactory.getLogger(NextFigurePublisher.class));
	}

	@Override
	public void subscribe(Subscriber<? super Message> subscriber) {
		this.logger.debug("===> NextFigurePublisher.subscribe()");
		super.subscribe(subscriber);
	}

	public void sendNextFigurePetition(Message message) {
		this.logger.debug("===> NextFigurePublisher.sendNextFigurePetition() = " + message);
		try {
			super.addToQueue(message);
		} catch (Exception e) {
			this.logger.error("=*=> ERROR: ", e);
		}
	}
}
