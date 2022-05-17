package com.dongyun.cnucinema.repository;

import com.dongyun.cnucinema.BaseIntegrityTest;
import com.dongyun.cnucinema.spec.entity.Actor;
import com.dongyun.cnucinema.spec.entity.Movie;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;

class DbMovieRepositoryTest implements BaseIntegrityTest {

    @Autowired DbMovieRepository movieRepository;

    @Test
    @DisplayName("영화 ID를 기준으로 존재하는 영화 정보를 정상적으로 가져와야 합니다.")
    void findByMid() {
        // given

        // when
        Movie movie = movieRepository.findByMid(2L).get();

        // then
        Assertions.assertThat(movie.getTitle()).contains("영화테스트2");
        Assertions.assertThat(movie.getActors()).hasSize(6);
        Assertions.assertThat(movie.getActors()).contains(
                Actor.builder().mid(2L).name("배우2_1").build(),
                Actor.builder().mid(2L).name("배우2_6").build()
        );
    }

    @Test
    @DisplayName("영화 ID를 기준으로 존재하지 않는 영화 정보를 가져오면 오류가 발생하여야 합니다.")
    void findByNonExistingMid() {
        // given

        // when

        // then
        Assertions.assertThat(movieRepository.findByMid(123456L).isEmpty()).isTrue();
    }

    @Test
    @DisplayName("영화 제목의 일부를 기준으로 존재하는 영화 정보를 정상적으로 가져와야 합니다.")
    void findByTitle() {
        // given

        // when
        List<Movie> movies = movieRepository.findByTitleContains("영화테스트");

        // then
        Assertions.assertThat(movies).hasSize(4);
        Assertions.assertThat(movies)
                .extracting("title").contains("영화테스트1");
        Assertions.assertThat(
                movies.stream().filter(
                    m -> m.getTitle().equals("영화테스트1")).findAny().get().getActors())
                .hasSize(4);
        Assertions.assertThat(
                movies.stream().filter(
                    m -> m.getTitle().equals("영화테스트1")).findAny().get().getActors())
                .contains(
                        Actor.builder().mid(1L).name("배우1_1").build(),
                        Actor.builder().mid(1L).name("배우1_4").build()
                );
    }

    @Test
    @DisplayName("영화 관람일을 기준으로 존재하는 영화 정보를 정상적으로 가져와야 합니다.")
    void findBySchedule() {
        // given

        // when
        List<Movie> movies = movieRepository.findByScheduleShowAtDate(LocalDate.parse("2022-05-09"));

        // then
        Assertions.assertThat(movies).hasSize(2);
        Assertions.assertThat(movies).extracting("title").containsOnly("영화테스트2", "영화테스트4");
    }

    @Test
    @DisplayName("영화 제목의 일부와 영화 관람일을 기준으로 존재하는 영화 정보를 정상적으로 가져와야 합니다.")
    void findByTitleAndSchedule() {
        // given

        // when
        List<Movie> movies = movieRepository.findByTitleContainsAndScheduleShowAtDate("영화테스트", LocalDate.parse("2022-05-09"));

        // then
        Assertions.assertThat(movies).hasSize(2);
        Assertions.assertThat(movies).extracting("title").containsOnly("영화테스트2", "영화테스트4");
    }
}