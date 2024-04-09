package org.jmedina.jtetris.engine.service;

import org.jmedina.jtetris.engine.model.FigureOperation;

import reactor.core.publisher.Flux;

public interface ConversationService {

	public Flux<FigureOperation> getFigureConversation();

}
