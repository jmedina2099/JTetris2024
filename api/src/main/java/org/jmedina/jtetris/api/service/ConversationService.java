package org.jmedina.jtetris.api.service;

import org.jmedina.jtetris.common.model.BoardOperation;
import org.jmedina.jtetris.common.model.FigureOperation;

import reactor.core.publisher.Flux;

public interface ConversationService {

	public Flux<FigureOperation> getFigureConversation();

	public Flux<BoardOperation> getBoardConversation();
}
