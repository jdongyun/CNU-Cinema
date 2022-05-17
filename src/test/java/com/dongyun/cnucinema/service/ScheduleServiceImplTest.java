package com.dongyun.cnucinema.service;

import com.dongyun.cnucinema.BaseIntegrityTest;
import com.dongyun.cnucinema.dto.ScheduleCreateRequest;
import com.dongyun.cnucinema.dto.ScheduleDto;
import com.dongyun.cnucinema.spec.entity.Movie;
import com.dongyun.cnucinema.spec.entity.Schedule;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

class ScheduleServiceImplTest implements BaseIntegrityTest {

    @Autowired ScheduleServiceImpl scheduleService;

    @Autowired MovieServiceImpl movieService;

    @Autowired DateTimeFormatter dateTimeFormatter;

    @Test
    @DisplayName("영화관에서 상영 중인 시간과 겹치지 않는 스케줄을 추가할 수 있어야 합니다.")
    void create() {
        // given
        ScheduleCreateRequest request = ScheduleCreateRequest.builder()
                .mid(1L)
                .tname("경기")
                .showAt("2022-01-01 00:00:00")
                .build();
        Movie movie = movieService.findByMid(1L).get();

        // when
        Long sid = scheduleService.create(request, movie);
        Schedule schedule = scheduleService.findBySid(sid).get();

        // then
        Assertions.assertThat(schedule.getShowAt()).isEqualTo(
                LocalDateTime.of(2022, 1, 1, 0, 0, 0));
    }

    @Test
    @DisplayName("영화관에서 상영 중인 시간과 겹치는 스케줄을 추가할 수 없어야 합니다.")
    void duplicate() {
        // given
        ScheduleCreateRequest request = ScheduleCreateRequest.builder()
                .mid(1L)
                .tname("경기")
                .showAt("2022-05-09 09:30:00")
                .build();
        Movie movie = movieService.findByMid(1L).get();

        // when

        // then
        Assertions.assertThatThrownBy(() -> scheduleService.create(request, movie))
                .hasMessageContaining("시간표에 중복이 있습니다.");
    }

}