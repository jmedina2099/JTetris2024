package org.jmedina.jtetris.figures.service;

import org.jmedina.jtetris.figures.model.Message;

import reactor.core.publisher.Flux;

public interface ConversationService {

	public Flux<Message> getNextFigureConversation();

}
