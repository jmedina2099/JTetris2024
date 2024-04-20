package org.jmedina.jtetris.engine.service;

import org.jmedina.jtetris.common.model.FigureOperation;
import org.jmedina.jtetris.engine.figure.BoxMotion;
import org.jmedina.jtetris.engine.figure.FigureMotion;

import reactor.core.publisher.Flux;

public interface ConversationService {

	public Flux<FigureOperation<BoxMotion, FigureMotion<BoxMotion>>> getFigureConversation(String auth);

}
