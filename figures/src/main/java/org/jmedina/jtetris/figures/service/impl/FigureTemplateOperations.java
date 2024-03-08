package org.jmedina.jtetris.figures.service.impl;

import org.jmedina.jtetris.figures.model.Figura;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

/**
 * @author Jorge Medina
 *
 */
@RequiredArgsConstructor
@Service
public class FigureTemplateOperations {
 
    private final ReactiveMongoTemplate template;

    public Flux<Figura> findAll() {
        return template.findAll(Figura.class);
    } 
}