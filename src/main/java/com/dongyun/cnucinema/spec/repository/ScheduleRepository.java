package com.dongyun.cnucinema.spec.repository;

import com.dongyun.cnucinema.spec.entity.Schedule;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ScheduleRepository {

    Optional<Schedule> findBySid(Long id);

    List<Schedule> findByMid(Long id);

    List<Schedule> findByTname(String tname);

    List<Schedule> findByTnameWithShowAtBetween(String tname, LocalDateTime showAtStart, LocalDateTime showAtEnd);

    List<Schedule> findAll();

    void save(Schedule schedule);
}
