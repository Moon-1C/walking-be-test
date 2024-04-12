package com.walking.api.domain.checklist.model.checkwith;

import com.walking.api.ApiApp;
import com.walking.api.domain.checklist.model.checkwith.command.SaveCheckWithCommand;
import com.walking.api.domain.checklist.model.checkwith.command.UpdateTitleCheckWithCommand;
import com.walking.api.persistence.checkwith.CheckWithJpaRepository;
import com.walking.data.entity.checkwith.CheckWithEntity;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@ActiveProfiles(profiles = {"test"})
@SpringBootTest(classes = {ApiApp.class})
class CheckWithConverterTest {

	@Autowired CheckWithConverter checkWithConverter;

	@Autowired CheckWithJpaRepository checkWithJpaRepository;

	CheckWithEntity entity;

	String TITLE = "title";

	@BeforeEach
	void setUp() {
		String uid = getUID();
		entity = checkWithJpaRepository.save(CheckWithEntity.builder().title(TITLE).uid(uid).build());
	}

	String getUID() {
		Random random = new Random();
		String uid;
		while (true) {
			List<String> list = new ArrayList<>();
			for (int i = 0; i < 16; i++) {
				list.add(String.valueOf(random.nextInt(10)));
			}
			for (int i = 0; i < 16; i++) {
				list.add(String.valueOf((char) (random.nextInt(26) + 65)));
			}

			Collections.shuffle(list);
			uid = list.toString().split("[\\[\\]]")[1].replaceAll(", ", "");
			if (!checkWithJpaRepository.existsByUidAndDeletedFalse(uid)) {
				break;
			}
		}
		return uid;
	}

	@Test
	@DisplayName("엔티티를 모델로 변환합니다.")
	void convert_entity_to_model() {
		// Given

		// When
		CheckWith model = checkWithConverter.from(entity);

		// Then
		Assertions.assertEquals(entity.getTitle(), model.getTitle().get());
		Assertions.assertEquals(entity.getUid(), model.getUid().get());
	}

	@Test
	@DisplayName("엔티티들을 모델 리스트로 변환합니다.")
	void convert_entities_to_models() {
		// Given
		String uid1 = getUID();
		String uid2 = getUID();
		List<String> uids = List.of(uid1, uid2);
		CheckWithEntity entity1 = CheckWithEntity.builder().title(TITLE + 1).uid(uid1).build();
		CheckWithEntity entity2 = CheckWithEntity.builder().title(TITLE + 2).uid(uid2).build();
		List<CheckWithEntity> entities = List.of(entity1, entity2);

		// when
		List<CheckWith> models = checkWithConverter.from(entities);
		int idx = 0;

		// Then
		for (CheckWith model : models) {
			Assertions.assertTrue(model.getTitle().get().contains((idx + 1) + ""));
			Assertions.assertEquals(model.getUid().get(), uids.get(idx));
			idx++;
		}
	}

	@Test
	@DisplayName("모델을 DTO로 변환합니다.")
	void model_to_dto() {
		// Given
		CheckWith model = checkWithConverter.from(entity);

		// When
		CheckWithDto dto = checkWithConverter.to(model);

		// Then
		Assertions.assertEquals(model.getId(), dto.getId());
		Assertions.assertEquals(model.getTitle().get(), dto.getTitle());
		Assertions.assertEquals(model.getUid().get(), dto.getUid());
	}

	@Test
	@Transactional
	@DisplayName("저장 커멘드 객체를 생성하고 저장합니다.")
	void create_by_save_command() {
		// Given
		String uid = getUID();
		SaveCheckWithCommand command = SaveCheckWithCommand.of(TITLE, uid);

		// When
		CheckWithEntity saved = checkWithJpaRepository.save(checkWithConverter.to(command));

		// Then
		Assertions.assertNotNull(saved.getId());
		Assertions.assertEquals(command.getTitle().get(), saved.getTitle());
		Assertions.assertEquals(command.getUid().get(), saved.getUid());
	}

	@Test
	@Rollback
	@Transactional
	@DisplayName("제목 업데이트 커멘드 객체를 생성하고 수정합니다.")
	void update_by_update_title_command() {
		// Given
		entity = checkWithJpaRepository.findById(entity.getId()).orElse(null);
		CheckWith model = checkWithConverter.from(entity);
		model = model.updateTitle("title is updated");
		UpdateTitleCheckWithCommand command = UpdateTitleCheckWithCommand.of(model);

		// When
		CheckWithEntity updatedEntity = checkWithConverter.to(command);

		// Then
		Assertions.assertEquals(model.getId(), updatedEntity.getId());
		Assertions.assertEquals(model.getTitle().get(), updatedEntity.getTitle());
	}
}
