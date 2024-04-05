package org.jmedina.jtetris.engine.publisher;

import org.jmedina.jtetris.engine.model.BoardOperation;
import org.reactivestreams.Subscriber;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * @author Jorge Medina
 *
 */
@Service
public class BoardPublisher extends CustomPublisher<BoardOperation> {

	public BoardPublisher() {
		super();
		super.logger = LoggerFactory.getLogger(this.getClass());
	}

	@Override
	public void subscribe(Subscriber<? super BoardOperation> subscriber) {
		this.logger.debug("===> BoardPublisher.subscribe()");
		super.subscribe(subscriber);
	}

	public void sendBoard(BoardOperation boardOperation) {
		this.logger.debug("===> BoardPublisher.sendBoard() = " + boardOperation);
		try {
			addToQueueSync(boardOperation);
		} catch (Exception e) {
			this.logger.error("=*=> ERROR: ", e);
		}
	}

	private synchronized void addToQueueSync(BoardOperation boardOperation) {
		super.addToQueue(boardOperation);
	}

}
