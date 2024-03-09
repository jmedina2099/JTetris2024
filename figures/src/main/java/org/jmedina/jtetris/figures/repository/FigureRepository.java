package org.jmedina.jtetris.figures.repository;

import org.jmedina.jtetris.figures.model.Figura;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

/**
 * @author Jorge Medina
 *
 */
public interface FigureRepository extends ReactiveMongoRepository<Figura, String> {
}