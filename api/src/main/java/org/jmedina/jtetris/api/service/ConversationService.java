package org.jmedina.jtetris.api.service;

import org.jmedina.jtetris.common.model.BoardOperation;
import org.jmedina.jtetris.common.model.BoxDTO;
import org.jmedina.jtetris.common.model.FigureDTO;
import org.jmedina.jtetris.common.model.FigureOperation;

import reactor.core.publisher.Flux;

public interface ConversationService {

	public Flux<FigureOperation<FigureDTO>> getFigureConversation();

	public Flux<BoardOperation<BoxDTO>> getBoardConversation();
}
