package org.jmedina.jtetris.engine.publisher;

import org.jmedina.jtetris.engine.model.BoardOperation;
import org.reactivestreams.Subscriber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

/**
 * @author Jorge Medina
 *
 */
@RequiredArgsConstructor
@Service
public class BoardPublisher extends CustomPublisher<BoardOperation> {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
	public void subscribe(Subscriber<? super BoardOperation> subscriber) {
		this.logger.debug("===> BoardPublisher.subscribe()");
		super.subscribe(subscriber);
	}

	public void sendBoard(BoardOperation board) {
		this.logger.debug("===> BoardPublisher.sendBoard() = " + board);
		super.addToQueue(board);
	}

}
