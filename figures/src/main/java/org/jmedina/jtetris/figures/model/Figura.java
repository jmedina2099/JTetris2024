package org.jmedina.jtetris.figures.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author Jorge Medina
 *
 */
@NoArgsConstructor
@Getter
@Setter
@Document("figuras")
public class Figura {

	@Id
	private String id;
	private String name;
	private String boxes;

}