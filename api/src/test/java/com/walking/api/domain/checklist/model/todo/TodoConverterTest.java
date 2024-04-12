package com.walking.api.domain.checklist.model.todo;

import static com.walking.data.entity.checkwith.CheckStatus.UNCHECKED;

import com.walking.api.ApiApp;
import com.walking.api.domain.checklist.model.todo.command.DeleteTodoCommand;
import com.walking.api.domain.checklist.model.todo.command.SaveTodoCommand;
import com.walking.api.domain.checklist.model.todo.command.UpdateCheckTodoCommand;
import com.walking.api.domain.checklist.model.todo.command.UpdateDescriptionTodoCommand;
import com.walking.api.persistence.checkwith.TodoJpaRepository;
import com.walking.data.entity.checkwith.TodoEntity;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@ActiveProfiles(profiles = {"test"})
@SpringBootTest(classes = {ApiApp.class})
class TodoConverterTest {

	@Autowired TodoConverter todoConverter;

	@Autowired TodoJpaRepository todoJpaRepository;

	static Long CHECK_WITH_FK = 1L;
	static Long WRITER_ID = 1L;

	TodoEntity entity;

	@BeforeEach
	@Transactional
	void setUp() {
		entity =
				todoJpaRepository.save(
						TodoEntity.builder()
								.checkWithFk(CHECK_WITH_FK)
								.writerId(WRITER_ID)
								.description("this is description")
								.build());
	}

	@Test
	@DisplayName("엔티티를 모델로 변환합니다.")
	void convert_entity_to_model() {
		// Given

		// When
		Todo model = todoConverter.from(entity);

		// Then
		Assertions.assertEquals(entity.getWriterId(), model.getWriterId());
		Assertions.assertEquals(entity.getCheckWithFk(), model.getCheckWithId());
		Assertions.assertEquals(entity.getDescription(), model.getDescription().get());
		Assertions.assertFalse(model.isChecked());
	}

	@Test
	@DisplayName("엔티티들을 모델 리스트로 변환합니다.")
	void convert_entities_to_models() {
		// Given
		TodoEntity entity1 =
				TodoEntity.builder()
						.checkWithFk(CHECK_WITH_FK)
						.writerId(WRITER_ID)
						.description("this is writer " + WRITER_ID + "'s description")
						.build();
		TodoEntity entity2 =
				TodoEntity.builder()
						.checkWithFk(CHECK_WITH_FK + 1)
						.writerId(WRITER_ID + 1)
						.description("this is writer " + (WRITER_ID + 1) + "'s description")
						.build();
		List<TodoEntity> entities = List.of(entity1, entity2);

		// When
		List<Todo> models = todoConverter.from(entities);
		Long idx = 1L;

		// Then
		for (Todo model : models) {
			Assertions.assertNotNull(model.getId());
			Assertions.assertEquals(idx, model.getWriterId());
			Assertions.assertEquals(idx, model.getCheckWithId());
			Assertions.assertTrue(model.getDescription().get().contains(idx + ""));
			Assertions.assertFalse(model.isChecked());
			idx++;
		}
	}

	@Test
	@DisplayName("모델을 DTO로 변환합니다.")
	void model_to_dto() {
		// Given
		Todo model = todoConverter.from(entity);

		// When
		TodoDto dto = todoConverter.to(model);

		// Then
		Assertions.assertEquals(model.getId(), dto.getId());
		Assertions.assertEquals(model.getCheckWithId(), dto.getCheckWithId());
		Assertions.assertEquals(model.getWriterId(), dto.getWriterId());
		Assertions.assertEquals(model.getCheckerId(), dto.getCheckerId());
		Assertions.assertEquals(model.getDescription().get(), dto.getDescription());
		Assertions.assertEquals(model.getStatus().name(), dto.getStatus());
	}

	@Test
	@Transactional
	@DisplayName("저장 커멘드 객체를 생성하고 저장합니다.")
	void create_by_save_command() {
		// Given
		SaveTodoCommand command =
				SaveTodoCommand.of(CHECK_WITH_FK, WRITER_ID, "this is saved by Command Object");

		// When
		TodoEntity saved = todoJpaRepository.save(todoConverter.to(command));

		// Then
		Assertions.assertNotNull(saved.getId());
		Assertions.assertEquals(command.getCheckWithId(), saved.getCheckWithFk());
		Assertions.assertEquals(command.getWriterId(), saved.getWriterId());
		Assertions.assertEquals(UNCHECKED, saved.getStatus());
		Assertions.assertEquals(0L, saved.getCheckerId());
		Assertions.assertEquals(command.getDescription().get(), saved.getDescription());
	}

	@Test
	@Transactional
	@DisplayName("삭제 커멘드 객체를 생성하고 삭제합니다.")
	void delete_by_delete_command() {
		// Given
		Todo model = todoConverter.from(entity);
		DeleteTodoCommand command = DeleteTodoCommand.of(model);

		// When
		todoJpaRepository.delete(todoConverter.to(command));
		Optional<TodoEntity> deletedEntity = todoJpaRepository.findById(model.getId());

		// Then
		Assertions.assertTrue(deletedEntity.isEmpty());
	}

	@Test
	@Transactional
	@DisplayName("체크 업데이트 커멘드 객체를 생성하고 수정합니다.")
	void update_by_update_check_command() {
		// Given
		Todo model = todoConverter.from(entity);
		model = model.check(1L);
		UpdateCheckTodoCommand command = UpdateCheckTodoCommand.of(model);

		// When
		TodoEntity updatedEntity = todoJpaRepository.save(todoConverter.to(command));

		// Then
		Assertions.assertEquals(model.getId(), updatedEntity.getId());
		Assertions.assertEquals(model.getCheckerId(), updatedEntity.getCheckerId());
	}

	@Test
	@Transactional
	@DisplayName("설명 업데이트 커멘드 객체를 생성하고 수정합니다.")
	void update_by_update_description_command() {
		// Given
		Todo model = todoConverter.from(entity);
		model = model.updateDescription("this is updated Description");
		UpdateDescriptionTodoCommand command = UpdateDescriptionTodoCommand.of(model);

		// When
		TodoEntity updatedEntity = todoJpaRepository.save(todoConverter.to(command));

		// Then
		Assertions.assertEquals(model.getId(), updatedEntity.getId());
		Assertions.assertEquals(model.getDescription().get(), updatedEntity.getDescription());
	}
}
