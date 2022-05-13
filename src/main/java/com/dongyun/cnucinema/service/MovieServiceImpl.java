package com.dongyun.cnucinema.service;

import com.dongyun.cnucinema.spec.entity.Movie;
import com.dongyun.cnucinema.spec.repository.MovieRepository;
import com.dongyun.cnucinema.spec.service.MovieService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class MovieServiceImpl implements MovieService {
    private final MovieRepository movieRepository;

    public MovieServiceImpl(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    @Override
    public Optional<Movie> findByMid(Long id) {
        return this.movieRepository.findByMid(id);
    }

    @Override
    public List<Movie> findByTitleContains(String title) {
        return movieRepository.findByTitleContains(title);
    }

    @Override
    public List<Movie> findByScheduleShowAtDate(LocalDate showAtDate) {
        return movieRepository.findByScheduleShowAtDate(showAtDate);
    }

    @Override
    public List<Movie> findByTitleContainsAndScheduleShowAtDate(String title, LocalDate showAtDate) {
        return movieRepository.findByTitleContainsAndScheduleShowAtDate(title, showAtDate);
    }

    @Override
    public List<Movie> findAll() {
        return movieRepository.findAll();
    }
}
