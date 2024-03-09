package org.jmedina.jtetris.figures.repository;

import org.jmedina.jtetris.figures.domain.Figura;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

/**
 * @author Jorge Medina
 *
 */
public interface FigureRepository extends ReactiveMongoRepository<Figura, String> {
}