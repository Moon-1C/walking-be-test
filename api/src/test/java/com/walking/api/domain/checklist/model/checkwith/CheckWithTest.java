package com.walking.api.domain.checklist.model.checkwith;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CheckWithTest {

	@Test
	@DisplayName("제목을 수정합니다.")
	void updateTitle() {
		// Given
		CheckWith checkWith = CheckWith.of(1L, "title", "uid");

		// When
		String updateTitle = "update title";
		checkWith.updateTitle(updateTitle);

		// Then
		Assertions.assertEquals(checkWith.getTitle().get(), updateTitle);
	}
}
