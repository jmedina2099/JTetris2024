package org.jmedina.jtetris.engine.publisher;

import org.apache.logging.log4j.LogManager;
import org.jmedina.jtetris.common.model.BoardOperation;
import org.jmedina.jtetris.common.publisher.CustomPublisher;
import org.jmedina.jtetris.engine.figure.BoxMotion;
import org.reactivestreams.Subscriber;
import org.springframework.stereotype.Service;

/**
 * @author Jorge Medina
 *
 */
@Service
public class BoardPublisher extends CustomPublisher<BoardOperation<BoxMotion>> {

	public BoardPublisher() {
		super(LogManager.getLogger(BoardPublisher.class));
	}

	@Override
	public void subscribe(Subscriber<? super BoardOperation<BoxMotion>> subscriber) {
		this.logger.debug("===> BoardPublisher.subscribe()");
		super.subscribe(subscriber);
	}

	public void sendBoard(BoardOperation<BoxMotion> boardOperation) {
		this.logger.debug("===> BoardPublisher.sendBoard() = " + boardOperation);
		try {
			super.addToQueue(boardOperation);
		} catch (Exception e) {
			this.logger.error("=*=> ERROR: ", e);
		}
	}
}
