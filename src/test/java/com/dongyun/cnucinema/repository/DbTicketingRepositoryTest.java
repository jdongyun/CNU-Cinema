package com.dongyun.cnucinema.repository;

import com.dongyun.cnucinema.BaseIntegrityTest;
import com.dongyun.cnucinema.dto.TicketingCompletionRequest;
import com.dongyun.cnucinema.dto.TicketingDto;
import com.dongyun.cnucinema.spec.entity.Schedule;
import com.dongyun.cnucinema.spec.entity.Ticketing;
import com.dongyun.cnucinema.spec.enums.TicketingStatus;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;

class DbTicketingRepositoryTest implements BaseIntegrityTest {

    @Autowired DbTicketingRepository ticketingRepository;

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
        Assertions.assertThat(ticketings).hasSize(4);
    }

    @Test
    @DisplayName("사용자 별 티켓팅의 정보의 목록을 정상적으로 불러와야 합니다.")
    void findByUsername() {
        // given

        // when
        List<Ticketing> ticketings = ticketingRepository.findByUsername("test1");

        // then
        Assertions.assertThat(ticketings).hasSize(8);
    }
}