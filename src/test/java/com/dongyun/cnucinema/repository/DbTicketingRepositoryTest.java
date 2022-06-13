package com.dongyun.cnucinema.repository;

import com.dongyun.cnucinema.BaseIntegrityTest;
import com.dongyun.cnucinema.dto.TicketingDto;
import com.dongyun.cnucinema.spec.entity.Ticketing;
import com.dongyun.cnucinema.spec.enums.TicketingStatus;
import com.dongyun.cnucinema.spec.vo.TicketingStatsVo;
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

class DbTicketingRepositoryTest implements BaseIntegrityTest {

    @Autowired DbTicketingRepository ticketingRepository;

    @BeforeEach
    void beforeEach() {
        LocalDateTime now = LocalDateTime.of(2022, 5, 9, 0, 0, 0);

        // 5월 9일 10시에 시작.
        ticketingRepository.save(Ticketing.builder()
                .username("test1").sid(1L).rcAt(now)
                .seats(7).status(TicketingStatus.R).build());

        ticketingRepository.save(Ticketing.builder()
                .username("test2").sid(1L).rcAt(now.plusSeconds(1))
                .seats(5).status(TicketingStatus.R).build());

        ticketingRepository.save(Ticketing.builder()
                .username("test3").sid(1L).rcAt(now.plusSeconds(2))
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
    @DisplayName("새로운 티켓팅의 정보가 정상적으로 추가되어야 합니다.")
    void insert() {
        // given
        TicketingDto dto = TicketingDto.builder()
                .sid(1L)
                .username("test1")
                .seats(5)
                .status(TicketingStatus.R)
                .rcAt(LocalDateTime.now())
                .build();

        // when
        Long id = ticketingRepository.save(TicketingDto.toEntity(dto));
        Ticketing ticketing = ticketingRepository.findById(id).get();

        // then
        Assertions.assertThat(ticketing.getSid()).isEqualTo(dto.getSid());
        Assertions.assertThat(ticketing.getSeats()).isEqualTo(dto.getSeats());
    }

    @Test
    @DisplayName("기존 티켓팅의 정보가 정상적으로 수정되어야 합니다.")
    void update() {
        // given
        Ticketing previousTicketing = ticketingRepository.findById(1L).get();
        TicketingDto dto = TicketingDto.create(previousTicketing);

        // when
        dto.setStatus(TicketingStatus.C);
        Long id = ticketingRepository.save(TicketingDto.toEntity(dto));

        Ticketing ticketing = ticketingRepository.findById(id).get();

        // then
        Assertions.assertThat(ticketing.getSid()).isEqualTo(dto.getSid());
        Assertions.assertThat(ticketing.getSeats()).isEqualTo(dto.getSeats());

    }

    @Test
    @DisplayName("스케줄 별 티켓팅의 정보의 목록을 정상적으로 불러와야 합니다.")
    void findBySid() {
        // given

        // when
        List<Ticketing> ticketings = ticketingRepository.findBySid(1L);

        // then
        Assertions.assertThat(ticketings).hasSize(3);
    }

    @Test
    @DisplayName("사용자 별 티켓팅의 정보의 목록을 정상적으로 불러와야 합니다.")
    void findByUsername() {
        // given

        // when
        List<Ticketing> ticketings = ticketingRepository.findByUsername("test1");

        // then
        Assertions.assertThat(ticketings).hasSize(7);
    }

    @Test
    @DisplayName("사용자 별 예매 완료한 티켓팅의 정보의 목록을 정상적으로 불러와야 합니다.")
    void findByUsernameAndReservedOrderByRcAtDesc() {
        try (MockedStatic<LocalDateTime> mock = Mockito.mockStatic(LocalDateTime.class, Mockito.CALLS_REAL_METHODS)) {
            // given
            LocalDateTime now = LocalDateTime.of(2022, 5, 9, 13, 30, 0);
            mock.when(LocalDateTime::now).thenReturn(now);

            // when
            List<Ticketing> ticketings = ticketingRepository.findByUsernameAndReservedOrderByRcAtDesc("test1");

            // then
            Assertions.assertThat(ticketings).hasSize(1);
            Assertions.assertThat(ticketings.stream().findFirst().get().getSeats()).isEqualTo(3);
        }
    }

    @Test
    @DisplayName("사용자 별 예매 취소한 티켓팅의 정보의 목록을 정상적으로 불러와야 합니다.")
    void findByUsernameAndCancelledOrderByRcAtDesc() {
        // given

        // when
        List<Ticketing> ticketings = ticketingRepository.findByUsernameAndCancelledOrderByRcAtDesc("test1");

        // then
        Assertions.assertThat(ticketings).hasSize(3);
        Assertions.assertThat(ticketings.stream().findFirst().get().getSeats()).isEqualTo(2);
    }

    @Test
    @DisplayName("사용자 별 관람 완료한 티켓팅의 정보의 목록을 정상적으로 불러와야 합니다.")
    void findByUsernameAndWatchedOrderByShowAtDesc() {
        try (MockedStatic<LocalDateTime> mock = Mockito.mockStatic(LocalDateTime.class, Mockito.CALLS_REAL_METHODS)) {
            // given
            LocalDateTime now = LocalDateTime.of(2022, 5, 9, 13, 30, 0);
            mock.when(LocalDateTime::now).thenReturn(now);

            // when
            List<Ticketing> ticketings = ticketingRepository.findByUsernameAndWatchedOrderByShowAtDesc("test1");

            // then
            Assertions.assertThat(ticketings).hasSize(3);
            Assertions.assertThat(ticketings.stream().findFirst().get().getSeats()).isEqualTo(5);
        }
    }

    @Test
    @DisplayName("사용자 별 예매 완료한 기간별 티켓팅의 정보의 목록을 정상적으로 불러와야 합니다.")
    void findByUsernameAndReservedAndRcAtBetweenOrderByRcAtDesc() {
        try (MockedStatic<LocalDateTime> mock = Mockito.mockStatic(LocalDateTime.class, Mockito.CALLS_REAL_METHODS)) {
            // given
            LocalDateTime now = LocalDateTime.of(2022, 5, 9, 9, 30, 0);
            mock.when(LocalDateTime::now).thenReturn(now);

            // when
            List<Ticketing> ticketings = ticketingRepository.findByUsernameAndReservedAndRcAtBetweenOrderByRcAtDesc(
                    "test1", LocalDateTime.of(2022, 5, 9, 0, 0, 0),
                    LocalDateTime.of(2022, 5, 9, 0, 0, 4));

            // then
            Assertions.assertThat(ticketings).hasSize(2);
            Assertions.assertThat(ticketings.stream().findFirst().get().getSeats()).isEqualTo(3);
        }
    }

    @Test
    @DisplayName("사용자 별 예매 취소한 기간별 티켓팅의 정보의 목록을 정상적으로 불러와야 합니다.")
    void findByUsernameAndCancelledRcAtBetweenOrderByRcAtDesc() {
        // given

        // when
        List<Ticketing> ticketings = ticketingRepository.findByUsernameAndCancelledAndRcAtBetweenOrderByRcAtDesc(
                "test1", LocalDateTime.of(2022, 5, 9, 0, 0, 0),
                LocalDateTime.of(2022, 5, 9, 0, 0, 6));

        // then
        Assertions.assertThat(ticketings).hasSize(2);
        Assertions.assertThat(ticketings.stream().findFirst().get().getSeats()).isEqualTo(1);
    }

    @Test
    @DisplayName("사용자 별 관람 완료한 기간별 티켓팅의 정보의 목록을 정상적으로 불러와야 합니다.")
    void findByUsernameAndWatchedRcAtBetweenOrderByShowAtDesc() {
        try (MockedStatic<LocalDateTime> mock = Mockito.mockStatic(LocalDateTime.class, Mockito.CALLS_REAL_METHODS)) {
            // given
            LocalDateTime now = LocalDateTime.of(2022, 5, 9, 13, 30, 0);
            mock.when(LocalDateTime::now).thenReturn(now);

            // when
            List<Ticketing> ticketings = ticketingRepository.findByUsernameAndWatchedAndRcAtBetweenOrderByShowAtDesc(
                    "test1", LocalDateTime.of(2022, 5, 9, 10, 0, 0),
                    LocalDateTime.of(2022, 5, 9, 13, 0, 5));

            // then
            Assertions.assertThat(ticketings).hasSize(3);
            Assertions.assertThat(ticketings.stream().findFirst().get().getSeats()).isEqualTo(5);
        }
    }

    @Test
    @DisplayName("특정 기간동안 티켓팅 결과의 통계를 정상적으로 불러와야 합니다.")
    void findTicketingStatsByRcAtBetween() {
        // given

        // when
        List<TicketingStatsVo> stats = ticketingRepository.findTicketingStatsByRcAtBetween(
                LocalDate.of(2022, 5, 9), LocalDate.of(2022, 5, 9));

        // then
        Assertions.assertThat(stats).hasSize(9);
    }
}