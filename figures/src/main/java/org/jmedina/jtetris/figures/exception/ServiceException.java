package org.jmedina.jtetris.figures.exception;

/**
 * @author Jorge Medina
 *
 */
public class ServiceException extends RuntimeException {

	private static final long serialVersionUID = 5526448586336171777L;

	public ServiceException(Exception e) {
		super(e);
	}
}