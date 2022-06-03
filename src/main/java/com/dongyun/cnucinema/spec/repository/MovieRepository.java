package com.dongyun.cnucinema.spec.repository;

import com.dongyun.cnucinema.spec.entity.Movie;
import com.dongyun.cnucinema.spec.vo.MovieRankStatsVo;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MovieRepository {

    Optional<Movie> findByMid(Long id);

    List<Movie> findByTitleContains(String title);

    List<Movie> findByScheduleShowAtDate(LocalDate showAtDate);

    List<Movie> findByTitleContainsAndScheduleShowAtDate(String title, LocalDate showAtDate);

    List<Movie> findAll();

    List<MovieRankStatsVo> findByRcAtBetweenWithRank(LocalDate startDate, LocalDate endDate);
}
