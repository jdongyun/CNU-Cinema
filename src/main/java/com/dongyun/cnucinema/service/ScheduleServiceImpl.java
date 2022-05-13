package com.dongyun.cnucinema.service;

import com.dongyun.cnucinema.dto.ScheduleCreateRequest;
import com.dongyun.cnucinema.dto.ScheduleDto;
import com.dongyun.cnucinema.spec.entity.Movie;
import com.dongyun.cnucinema.spec.entity.Schedule;
import com.dongyun.cnucinema.spec.repository.ScheduleRepository;
import com.dongyun.cnucinema.spec.service.ScheduleService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ScheduleServiceImpl implements ScheduleService {
    private final ScheduleRepository scheduleRepository;

    public ScheduleServiceImpl(ScheduleRepository scheduleRepository) {
        this.scheduleRepository = scheduleRepository;
    }

    @Override
    public Optional<Schedule> findBySid(Long id) {
        return scheduleRepository.findBySid(id);
    }

    @Override
    public List<Schedule> findByMid(Long id) {
        return scheduleRepository.findByMid(id);
    }

    @Override
    public List<Schedule> findByTname(String tname) {
        return scheduleRepository.findByTname(tname);
    }

    @Override
    public List<Schedule> findAll() {
        return scheduleRepository.findAll();
    }

    @Override
    public void create(ScheduleCreateRequest request, Movie movie) {
        LocalDateTime showAtStart = LocalDateTime.parse(request.getShowAt()).minusMinutes(30);
        LocalDateTime showAtEnd = LocalDateTime.parse(request.getShowAt()).plusMinutes(movie.getLength() + 30);

        validateScreenTime(request.getTname(), showAtStart, showAtEnd);

        ScheduleDto schedule = ScheduleDto.builder()
                .mid(movie.getMid())
                .tname(request.getTname())
                .showAt(LocalDateTime.parse(request.getShowAt()))
                .build();

        scheduleRepository.save(ScheduleDto.toEntity(schedule));
    }

    @Override
    public void save(ScheduleDto scheduleDto) {
        scheduleRepository.save(ScheduleDto.toEntity(scheduleDto));
    }

    private void validateScreenTime(String tname, LocalDateTime showAtStart, LocalDateTime showAtEnd) {
        scheduleRepository.findByTnameWithShowAtBetween(tname, showAtStart, showAtEnd).stream()
                .findAny().ifPresent(c -> {
                    throw new IllegalStateException("시간표에 중복이 있습니다.");
                });
    }
}
