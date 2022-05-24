package com.dongyun.cnucinema.repository;

import com.dongyun.cnucinema.BaseIntegrityTest;
import com.dongyun.cnucinema.spec.entity.Actor;
import com.dongyun.cnucinema.spec.entity.Movie;
import com.dongyun.cnucinema.spec.entity.Ticketing;
import com.dongyun.cnucinema.spec.enums.TicketingStatus;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

class DbMovieRepositoryTest implements BaseIntegrityTest {

    @Autowired DbMovieRepository movieRepository;

    @Autowired DbTicketingRepository ticketingRepository;

    @BeforeEach
    void beforeEach() {
        LocalDateTime now = LocalDateTime.of(2022, 5, 9, 0, 0, 0);

        // 5월 9일 10시에 시작.
        ticketingRepository.save(Ticketing.builder()
                .username("test1").sid(1L).rcAt(now)
                .seats(7).status(TicketingStatus.R).build());

        ticketingRepository.save(Ticketing.builder()
                .username("test2").sid(7L).rcAt(now.plusSeconds(1))
                .seats(5).status(TicketingStatus.R).build());

        ticketingRepository.save(Ticketing.builder()
                .username("test3").sid(8L).rcAt(now.plusSeconds(2))
                .seats(2).status(TicketingStatus.R).build());

        // 5월 9일 12시에 시작.
        ticketingRepository.save(Ticketing.builder()
                .username("test1").sid(3L).rcAt(now.plusSeconds(3))
                .seats(3).status(TicketingStatus.R).build());

        // 5월 9일 13시에 시작.
        ticketingRepository.save(Ticketing.builder()
                .username("test1").sid(2L).rcAt(now.plusSeconds(4))
                .seats(3).status(TicketingStatus.C).build());
        ticketingRepository.save(Ticketing.builder()
                .username("test1").sid(2L).rcAt(now.plusSeconds(5))
                .seats(5).status(TicketingStatus.R).build());

        // 5월 9일 15시에 시작.
        ticketingRepository.save(Ticketing.builder()
                .username("test1").sid(4L).rcAt(now.plusSeconds(6))
                .seats(1).status(TicketingStatus.C).build());
        ticketingRepository.save(Ticketing.builder()
                .username("test1").sid(4L).rcAt(now.plusSeconds(7))
                .seats(2).status(TicketingStatus.C).build());
        ticketingRepository.save(Ticketing.builder()
                .username("test1").sid(4L).rcAt(now.plusSeconds(8))
                .seats(3).status(TicketingStatus.R).build());
    }

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

    @Test
    @DisplayName("영화 ID를 기준으로 상영 중인 영화의 관람객 수를 정상적으로 가져와야 합니다.")
    void findSeatsByReleasedMid() {
        try (MockedStatic<LocalDateTime> mock = Mockito.mockStatic(LocalDateTime.class, Mockito.CALLS_REAL_METHODS)) {
            // given
            LocalDateTime now = LocalDateTime.of(2022, 5, 9, 13, 30, 0);
            mock.when(LocalDateTime::now).thenReturn(now);

            // when
            Movie movie = movieRepository.findByMid(2L).get();

            // then
            Assertions.assertThat(movie.getTotalReservedSeats()).isEqualTo(3);
            Assertions.assertThat(movie.getTotalWatchedSeats()).isEqualTo(15);
        }
    }

    @Test
    @DisplayName("영화 ID를 기준으로 개봉 예정인 영화의 관람객 수를 정상적으로 가져와야 합니다.")
    void findSeatsByNotReleasedMid() {
        try (MockedStatic<LocalDateTime> mock = Mockito.mockStatic(LocalDateTime.class, Mockito.CALLS_REAL_METHODS)) {
            // given
            LocalDateTime now = LocalDateTime.of(2022, 5, 8, 13, 30, 0);
            mock.when(LocalDateTime::now).thenReturn(now);

            // when
            Movie movie = movieRepository.findByMid(2L).get();

            // then
            Assertions.assertThat(movie.getTotalReservedSeats()).isEqualTo(18);
        }
    }

    @Test
    @DisplayName("영화 제목의 일부를 기준으로 상영 중인 영화 정보를 정상적으로 가져와야 합니다.")
    void findReleasedMovieByTitle() {
        try (MockedStatic<LocalDateTime> mock = Mockito.mockStatic(LocalDateTime.class, Mockito.CALLS_REAL_METHODS)) {
            // given
            LocalDateTime now = LocalDateTime.of(2022, 5, 24, 13, 30, 0);
            mock.when(LocalDateTime::now).thenReturn(now);

            // when
            List<Movie> movies = movieRepository.findByTitleContains("영화테스트");

            // then
            Assertions.assertThat(
                            movies.stream().filter(
                                    m -> m.getTitle().equals("영화테스트3")).findAny().get().getTotalReservedSeats())
                    .isEqualTo(2);
            Assertions.assertThat(
                            movies.stream().filter(
                                    m -> m.getTitle().equals("영화테스트3")).findAny().get().getTotalWatchedSeats())
                    .isEqualTo(5);
        }
    }

    @Test
    @DisplayName("영화 제목의 일부와 영화 관람일을 기준으로 상영 중인 영화 정보를 정상적으로 가져와야 합니다.")
    void findReleasedMovieByTitleAndSchedule() {
        try (MockedStatic<LocalDateTime> mock = Mockito.mockStatic(LocalDateTime.class, Mockito.CALLS_REAL_METHODS)) {
            // given
            LocalDateTime now = LocalDateTime.of(2022, 5, 24, 13, 30, 0);
            mock.when(LocalDateTime::now).thenReturn(now);

            // when
            List<Movie> movies = movieRepository.findByTitleContainsAndScheduleShowAtDate("영화테스트", LocalDate.parse("2022-05-24"));

            // then
            Assertions.assertThat(movies).hasSize(1);
            Assertions.assertThat(
                            movies.stream().filter(
                                    m -> m.getTitle().equals("영화테스트3")).findAny().get().getTotalReservedSeats())
                    .isEqualTo(2);
            Assertions.assertThat(
                            movies.stream().filter(
                                    m -> m.getTitle().equals("영화테스트3")).findAny().get().getTotalWatchedSeats())
                    .isEqualTo(5);
        }
    }

    @Test
    @DisplayName("모든 영화의 정보를 정상적으로 가져와야 합니다.")
    void findAll() {
        try (MockedStatic<LocalDateTime> mock = Mockito.mockStatic(LocalDateTime.class, Mockito.CALLS_REAL_METHODS)) {
            // given
            LocalDateTime now = LocalDateTime.of(2022, 5, 24, 13, 30, 0);
            mock.when(LocalDateTime::now).thenReturn(now);

            // when
            List<Movie> movies = movieRepository.findAll();

            // then
            Assertions.assertThat(movies).hasSize(4);
            Assertions.assertThat(
                            movies.stream().filter(
                                    m -> m.getTitle().equals("영화테스트2")).findAny().get().getTotalWatchedSeats())
                    .isEqualTo(18);
            Assertions.assertThat(
                            movies.stream().filter(
                                    m -> m.getTitle().equals("영화테스트3")).findAny().get().getTotalReservedSeats())
                    .isEqualTo(2);
            Assertions.assertThat(
                            movies.stream().filter(
                                    m -> m.getTitle().equals("영화테스트3")).findAny().get().getTotalWatchedSeats())
                    .isEqualTo(5);
        }
    }
}