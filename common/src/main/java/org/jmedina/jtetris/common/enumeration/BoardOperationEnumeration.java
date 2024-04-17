package org.jmedina.jtetris.common.enumeration;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import lombok.Getter;

@Getter
public enum BoardOperationEnumeration {

	BOARD_WITH_ADDED_FIGURE(0), BOARD_WITH_OCURRED_ONE_LINE(1), BOARD_WITH_OCURRED_TWO_LINES(2),
	BOARD_WITH_OCURRED_THREE_LINES(3), BOARD_WITH_OCURRED_FOUR_LINES(4);

	private int numLinesMaded;

	BoardOperationEnumeration(int numLinesMaded) {
		this.numLinesMaded = numLinesMaded;
	}

	public static BoardOperationEnumeration getByNumLinesMaded(int numLinesMaded) {
		List<BoardOperationEnumeration> list = Arrays.asList(BoardOperationEnumeration.values()).stream()
				.filter(e -> e.getNumLinesMaded() == numLinesMaded).collect(Collectors.toList());
		return list.size() > 0 ? list.get(0) : null;
	}
}
