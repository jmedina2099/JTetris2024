package org.jmedina.jtetris.figures.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author Jorge Medina
 *
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Document("figuras")
public class Figura {

	@Id
	private String id;
	private String name;
	private String boxes;
}