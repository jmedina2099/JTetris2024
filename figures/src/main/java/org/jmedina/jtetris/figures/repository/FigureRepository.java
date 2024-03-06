package org.jmedina.jtetris.figures.repository;

import org.jmedina.jtetris.figures.model.Figura;
import org.springframework.data.repository.CrudRepository;

/**
 * @author Jorge Medina
 *
 */
public interface FigureRepository extends CrudRepository<Figura, Long> {}