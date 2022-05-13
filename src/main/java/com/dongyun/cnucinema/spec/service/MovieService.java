package com.dongyun.cnucinema.spec.service;

import com.dongyun.cnucinema.spec.entity.Movie;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MovieService {
    Optional<Movie> findByMid(Long id);

    List<Movie> findByTitleContains(String title);

    List<Movie> findByScheduleShowAtDate(LocalDate showAtDate);

    List<Movie> findByTitleContainsAndScheduleShowAtDate(String title, LocalDate showAtDate);

    List<Movie> findAll();
}
