package com.walking.api.domain.checklist.model.todo;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TodoTest {

	@Test
	@DisplayName("체크가 되어있지 않은 상태에서 체크 상태를 확인합니다.")
	void unchecked_isChecked() {
		// Given
		Todo todo = Todo.of(1L, 1L, 1L, 0L, "description", "UNCHECKED");

		// When
		boolean checked = todo.isChecked();

		// Then
		Assertions.assertFalse(checked);
	}

	@Test
	@DisplayName("체크가 되어 있는 상태에서 체크 상태를 확인합니다.")
	void checked_isChecked() {
		// Given
		Todo todo = Todo.of(1L, 1L, 1L, 0L, "description", "CHECKED");

		// When
		boolean checked = todo.isChecked();

		// Then
		Assertions.assertTrue(checked);
	}

	@Test
	@DisplayName("설명을 수정합니다.")
	void update_description() {
		// Given
		Todo todo = Todo.of(1L, 1L, 1L, 0L, "description", "CHECKED");

		// When
		String updateDescription = "this description is updated";
		todo = todo.updateDescription(updateDescription);

		// Then
		Assertions.assertEquals(todo.getDescription().get(), updateDescription);
	}

	@Test
	@DisplayName("체크가 되어있지 않은 상태에서 체크를 수행합니다.")
	void unchecked_check() {
		// Given
		Todo todo = Todo.of(1L, 1L, 1L, 0L, "description", "UNCHECKED");

		// When
		todo = todo.check(1L);

		// Then
		Assertions.assertTrue(todo.isChecked());
		Assertions.assertEquals(1L, todo.getCheckerId());
	}

	@Test
	@DisplayName("체크가 되어 있는 상태에서 체크 해제를 수행합니다.")
	void checked_uncheck() {
		// Given
		Todo todo = Todo.of(1L, 1L, 1L, 1L, "description", "CHECKED");

		// When
		todo = todo.unCheck();

		// Then
		Assertions.assertFalse(todo.isChecked());
		Assertions.assertEquals(0L, todo.getCheckerId());
	}

	@Test
	@DisplayName("올바른 작성자인 경우 작성자가 맞는지 확인합니다.")
	void correct_isWriter() {
		// Given
		Todo todo = Todo.of(1L, 1L, 1L, 0L, "description", "UNCHECKED");

		// When
		boolean isWriter = todo.isWriter(1L);

		// Then
		Assertions.assertTrue(isWriter);
	}

	@Test
	@DisplayName("올바르지 않은 작성자인 경우 작성자가 맞는지 확인합니다.")
	void incorrect_isWriter() {
		// Given
		Todo todo = Todo.of(1L, 1L, 1L, 0L, "description", "UNCHECKED");

		// When
		boolean isWriter = todo.isWriter(2L);

		// Then
		Assertions.assertFalse(isWriter);
	}

	@Test
	@DisplayName("올바른 체커인 경우 체커가 맞는지 확인합니다.")
	void correct_isChecker() {
		// Given
		Todo todo = Todo.of(1L, 1L, 1L, 1L, "description", "CHECKED");

		// When
		boolean isWriter = todo.isChecker(1L);

		// Then
		Assertions.assertTrue(isWriter);
	}

	@Test
	@DisplayName("올바르지 않은 체커인 경우 체커가 맞는지 확인합니다.")
	void incorrect_isChecker() {
		// Given
		Todo todo = Todo.of(1L, 1L, 1L, 1L, "description", "UNCHECKED");

		// When
		boolean isWriter = todo.isChecker(2L);

		// Then
		Assertions.assertFalse(isWriter);
	}
}
