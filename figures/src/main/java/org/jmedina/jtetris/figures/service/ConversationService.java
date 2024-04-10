package org.jmedina.jtetris.figures.service;

import org.jmedina.jtetris.figures.model.NextFigureOperation;

import reactor.core.publisher.Flux;

public interface ConversationService {

	public Flux<NextFigureOperation> getNextFigureConversation();

}
