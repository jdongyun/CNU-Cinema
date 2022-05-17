package com.dongyun.cnucinema.spec.service;

import com.dongyun.cnucinema.dto.ScheduleCreateRequest;
import com.dongyun.cnucinema.dto.ScheduleDto;
import com.dongyun.cnucinema.spec.entity.Movie;
import com.dongyun.cnucinema.spec.entity.Schedule;

import java.util.List;
import java.util.Optional;

public interface ScheduleService {

    Optional<Schedule> findBySid(Long id);

    List<Schedule> findByMid(Long id);

    List<Schedule> findByTname(String tname);

    List<Schedule> findAll();

    Long create(ScheduleCreateRequest request, Movie movie);

    Long save(ScheduleDto scheduleDto);
}
