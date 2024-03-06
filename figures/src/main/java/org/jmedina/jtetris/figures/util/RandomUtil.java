package org.jmedina.jtetris.figures.util;

import java.security.SecureRandom;

import org.springframework.stereotype.Service;

/**
 * @author Jorge Medina
 *
 */
@Service
public class RandomUtil {

	private final SecureRandom secureRandom = new SecureRandom();

	public int nextInt(int bound) {
		return this.secureRandom.nextInt(bound);
	}
}
