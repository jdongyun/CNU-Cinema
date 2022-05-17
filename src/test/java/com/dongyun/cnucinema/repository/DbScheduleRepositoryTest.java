package com.dongyun.cnucinema.repository;

import com.dongyun.cnucinema.BaseIntegrityTest;
import com.dongyun.cnucinema.dto.ScheduleDto;
import com.dongyun.cnucinema.spec.entity.Schedule;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

class DbScheduleRepositoryTest implements BaseIntegrityTest {

    @Autowired DbScheduleRepository scheduleRepository;

    @Test
    @DisplayName("스케줄 ID를 기준으로 존재하는 스케줄 정보를 정상적으로 가져와야 합니다.")
    void findBySid() {
        // given

        // when
        Schedule schedule = scheduleRepository.findBySid(1L).get();

        // then
        Assertions.assertThat(schedule.getSid()).isEqualTo(1L);
        Assertions.assertThat(schedule.getMid()).isEqualTo(2L);
    }

    @Test
    @DisplayName("스케줄의 남은 좌석수가 정상적으로 계산되어야 합니다.")
    void remainSeats() {
        // given

        // when
        Schedule schedule1 = scheduleRepository.findBySid(1L).get();
        Schedule schedule2 = scheduleRepository.findBySid(10L).get();

        // then
        Assertions.assertThat(schedule1.getRemainSeats()).isEqualTo(70-7-5-5);
        Assertions.assertThat(schedule2.getRemainSeats()).isEqualTo(65);
    }

    @Test
    @DisplayName("영화 ID를 기준으로 해당하는 스케줄의 목록을 모두 가져와야 합니다.")
    void findByMid() {
        // given

        // when
        List<Schedule> schedules = scheduleRepository.findByMid(2L);

        // then
        Assertions.assertThat(schedules).hasSize(7);
        Assertions.assertThat(schedules).extracting("sid").contains(1L, 2L, 6L, 18L);
    }

    @Test
    @DisplayName("영화관 이름을 기준으로 해당하는 스케줄의 목록을 모두 가져와야 합니다.")
    void findByTname() {
        // given

        // when
        List<Schedule> schedules = scheduleRepository.findByTname("서울");

        // then
        Assertions.assertThat(schedules).hasSize(3);
        Assertions.assertThat(schedules).extracting("sid").contains(7L, 8L, 18L);
    }

    @Test
    @DisplayName("영화관 이름과 상영일의 구간을 기준으로 해당하는 스케줄의 목록을 모두 가져와야 합니다.")
    void findByTnameWithShowAtBetween() {
        // given

        // when
        List<Schedule> schedules = scheduleRepository.findByTnameWithShowAtBetween(
                "세종",
                LocalDateTime.of(2022, 5, 9, 14, 0, 0),
                LocalDateTime.of(2022, 5, 10, 16, 0, 0)
        );

        // then
        Assertions.assertThat(schedules).hasSize(2);
        Assertions.assertThat(schedules).extracting("showAt").contains(
                LocalDateTime.of(2022, 5, 9, 15, 0, 0),
                LocalDateTime.of(2022, 5, 10, 15, 0, 0)
        );
    }

    @Test
    @DisplayName("새로운 스케줄의 정보가 올바르게 추가되어야 합니다.")
    void insert() {
        // given
        ScheduleDto scheduleDto = ScheduleDto.builder()
                .mid(1L)
                .tname("서울")
                .showAt(LocalDateTime.of(2020, 1, 1, 0, 0, 0))
                .build();

        // when
        Long sid = scheduleRepository.save(ScheduleDto.toEntity(scheduleDto));
        Schedule schedule = scheduleRepository.findBySid(sid).get();

        // then
        Assertions.assertThat(schedule).extracting("tname").isEqualTo("서울");
    }

    @Test
    @DisplayName("기존 스케줄의 정보가 올바르게 수정되어야 합니다.")
    void update() {
        // given
        Schedule previousSchedule = scheduleRepository.findBySid(1L).get();
        ScheduleDto scheduleDto = ScheduleDto.create(previousSchedule);
        scheduleDto.setShowAt(LocalDateTime.of(2020, 1, 1, 0, 0, 0));

        // when
        Long sid = scheduleRepository.save(ScheduleDto.toEntity(scheduleDto));
        Schedule schedule = scheduleRepository.findBySid(sid).get();

        // then
        Assertions.assertThat(schedule.getSid()).isEqualTo(1L);
        Assertions.assertThat(schedule.getShowAt()).isEqualTo(
                LocalDateTime.of(2020, 1, 1, 0, 0, 0));
    }
}