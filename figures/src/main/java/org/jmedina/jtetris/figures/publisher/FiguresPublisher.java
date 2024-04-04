package org.jmedina.jtetris.figures.publisher;

import org.jmedina.jtetris.figures.model.FigureOperation;
import org.jmedina.jtetris.figures.service.FigureService;
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
public class FiguresPublisher extends CustomPublisher<FigureOperation> {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private final FigureService figureService;

	@Override
	public void subscribe(Subscriber<? super FigureOperation> subscriber) {
		this.logger.debug("===> FiguresPublisher.subscribe()");
		super.subscribe(subscriber);
		askAndSendNextFigureOperation();
	}

	public void askAndSendNextFigureOperation() {
		this.logger.debug("===> FiguresPublisher.askAndSendNextFigureOperation()");
		super.addToQueue(this.figureService.askForNextFigureOperation());
	}
}
