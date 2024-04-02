package org.jmedina.jtetris.engine.publisher;

import org.jmedina.jtetris.engine.model.FigureOperation;
import org.jmedina.jtetris.engine.service.FigureService;
import org.reactivestreams.Publisher;
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
public class FigurePublisher implements Publisher<FigureOperation> {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private final FigureService figureService;
	private Subscriber<? super FigureOperation> subscriber;

	@Override
	public void subscribe(Subscriber<? super FigureOperation> subscriber) {
		this.logger.debug("===> FigurePublisher.subscribe()");
		this.subscriber = subscriber;
		getFigureConversation();
	}

	private void getFigureConversation() {
		this.logger.debug("===> FigurePublisher.getFigureConversation()");
		this.figureService.getFigureConversation().subscribe(this.subscriber);
	}

	public void askForNextFigure() {
		this.logger.debug("===> FigurePublisher.askForNextFigure()");
		this.figureService.askForNextFigure().subscribe();
	}

}
