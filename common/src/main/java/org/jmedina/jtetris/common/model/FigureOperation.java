package org.jmedina.jtetris.common.model;

import java.util.concurrent.atomic.AtomicReference;

import org.jmedina.jtetris.common.enumeration.FigureOperationEnumeration;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author Jorge Medina
 *
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@ToString
public class FigureOperation {

	private FigureOperationEnumeration operation;
	@ToString.Exclude
	private AtomicReference<? extends Figure> figure;
	private long initialTimeStamp;
	private long timeStamp;

	@JsonGetter
	@ToString.Include(name = "figure")
	public Figure getFigure() {
		return figure.get();
	}

	@SuppressWarnings("unchecked")
	@JsonSetter
	public <T extends Figure> void setFigure(T fig) {
		((AtomicReference<T>) this.figure).set(fig);
	}
}
