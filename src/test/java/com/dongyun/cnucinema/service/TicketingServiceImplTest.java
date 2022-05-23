package com.dongyun.cnucinema.service;

import com.dongyun.cnucinema.BaseIntegrityTest;
import com.dongyun.cnucinema.dto.CustomerDto;
import com.dongyun.cnucinema.dto.TicketingCancellationRequest;
import com.dongyun.cnucinema.dto.TicketingCompletionRequest;
import com.dongyun.cnucinema.spec.entity.Customer;
import com.dongyun.cnucinema.spec.enums.TicketingStatus;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.LocalDateTime;

class TicketingServiceImplTest implements BaseIntegrityTest {

    @Autowired TicketingServiceImpl ticketingService;

    @Autowired CustomerServiceImpl customerService;

    @Test
    @DisplayName("10자리를 초과하는 좌석은 티켓팅이 불가능하여야 합니다.")
    void maximumSeats() {
        try (MockedStatic<LocalDateTime> mock = Mockito.mockStatic(LocalDateTime.class, Mockito.CALLS_REAL_METHODS)) {
            // given
            LocalDateTime now = LocalDateTime.of(2022, 5, 9, 9, 10, 0);
            mock.when(LocalDateTime::now).thenReturn(now);

            TicketingCompletionRequest request = new TicketingCompletionRequest();
            request.setSid(1L);
            request.setSeats(11);

            // when

            // then
            Assertions.assertThatThrownBy(() ->
                            ticketingService.reserve(request, "test1"))
                    .hasMessageContaining("좌석");
        }
    }

    @Test
    @DisplayName("사용자의 연령보다 시청 가능 연령이 높은 영화는 티켓팅이 불가능하여야 합니다.")
    void minimumAge() {
        try (MockedStatic<LocalDateTime> mock = Mockito.mockStatic(LocalDateTime.class, Mockito.CALLS_REAL_METHODS)) {
            // given
            LocalDateTime now = LocalDateTime.of(2022, 5, 9, 9, 10, 0);
            mock.when(LocalDateTime::now).thenReturn(now);

            Customer prevCustomer = customerService.findOne("test1").get();
            CustomerDto customerDto = CustomerDto.create(prevCustomer);
            TicketingCompletionRequest request = new TicketingCompletionRequest();

            // when
            customerDto.setBirthDate(LocalDate.now().minusYears(1));
            customerService.save(customerDto);
            request.setSid(1L);
            request.setSeats(5);

            // then
            Assertions.assertThatThrownBy(() ->
                            ticketingService.reserve(request, "test1"))
                    .hasMessageContaining("연령");
        }
    }

    @Test
    @DisplayName("존재하지 않는 사용자로는 티켓팅을 할 수 없어야 합니다.")
    void reserveNonExistenceUser() {
        // given
        TicketingCompletionRequest dto = new TicketingCompletionRequest();
        dto.setSid(1L);
        dto.setSeats(5);

        // when

        // then
        Assertions.assertThatThrownBy(() -> ticketingService.reserve(dto, "non-existence-username"))
                .hasMessage("해당하는 사용자가 없습니다.");
    }

    @Test
    @DisplayName("존재하지 않는 스케줄로는 티켓팅을 할 수 없어야 합니다.")
    void reserveNonExistenceSchedule() {
        // given
        TicketingCompletionRequest dto = new TicketingCompletionRequest();
        dto.setSid(54321L);
        dto.setSeats(5);

        // when

        // then
        Assertions.assertThatThrownBy(() -> ticketingService.reserve(dto, "test1"))
                .hasMessage("해당하는 스케줄이 없습니다.");
    }

    @Test
    @DisplayName("상영 시작 시각이 지난 스케줄로는 티켓팅이 불가능하여야 합니다.")
    void scheduleBeforeNow() {
        try (MockedStatic<LocalDateTime> mock = Mockito.mockStatic(LocalDateTime.class, Mockito.CALLS_REAL_METHODS)) {
            // given
            LocalDateTime now = LocalDateTime.of(2022, 5, 9, 10, 10, 0);
            mock.when(LocalDateTime::now).thenReturn(now);

            TicketingCompletionRequest dto = new TicketingCompletionRequest();
            dto.setSid(1L);
            dto.setSeats(5);

            // when

            // then
            Assertions.assertThatThrownBy(() ->
                            ticketingService.reserve(dto, "test1"))
                    .hasMessage("상영 시작 시각이 지나 예매가 불가능합니다.");
        }
    }

    @Test
    @DisplayName("사용자가 정상적으로 티켓팅을 할 수 있어야 합니다.")
    void reserve() {
        try (MockedStatic<LocalDateTime> mock = Mockito.mockStatic(LocalDateTime.class, Mockito.CALLS_REAL_METHODS)) {
            // given
            LocalDateTime now = LocalDateTime.of(2022, 5, 9, 9, 0, 0);
            mock.when(LocalDateTime::now).thenReturn(now);

            TicketingCompletionRequest dto = new TicketingCompletionRequest();
            dto.setSid(1L);
            dto.setSeats(5);

            // when
            Long id = ticketingService.reserve(dto, "test1");

            // then
            Assertions.assertThat(ticketingService.findById(id).get().getSid()).isEqualTo(dto.getSid());
        }
    }

    @Test
    @DisplayName("존재하지 않는 예매 내역을 취소할 수 없어야 합니다.")
    void cancelNonExistenceTicketing() {
        // given
        TicketingCancellationRequest dto = new TicketingCancellationRequest();
        dto.setId(54321L);

        // when

        // then
        Assertions.assertThatThrownBy(() -> ticketingService.cancel(dto, "test1"))
                .hasMessage("해당하는 예매 내역이 없습니다.");
    }

    @Test
    @DisplayName("다른 사용자의 예매 내역을 취소할 수 없어야 합니다.")
    void cancelOtherUsers() {
        try (MockedStatic<LocalDateTime> mock = Mockito.mockStatic(LocalDateTime.class, Mockito.CALLS_REAL_METHODS)) {
            // given
            LocalDateTime now = LocalDateTime.of(2022, 5, 9, 9, 0, 0);
            mock.when(LocalDateTime::now).thenReturn(now);

            TicketingCompletionRequest completionRequest = new TicketingCompletionRequest();
            completionRequest.setSid(1L);
            completionRequest.setSeats(5);
            Long id = ticketingService.reserve(completionRequest, "test1");

            TicketingCancellationRequest dto = new TicketingCancellationRequest();
            dto.setId(id);

            // when

            // then
            Assertions.assertThatThrownBy(() -> ticketingService.cancel(dto, "test2"))
                    .hasMessage("본인의 예매 내역이 아닙니다.");
        }
    }

    @Test
    @DisplayName("이미 취소된 예매 내역을 취소할 수 없어야 합니다.")
    void cancelAlreadyCancelled() {
        try (MockedStatic<LocalDateTime> mock = Mockito.mockStatic(LocalDateTime.class, Mockito.CALLS_REAL_METHODS)) {
            // given
            LocalDateTime now = LocalDateTime.of(2022, 5, 9, 9, 30, 0);
            mock.when(LocalDateTime::now).thenReturn(now);

            TicketingCompletionRequest completionRequest = new TicketingCompletionRequest();
            completionRequest.setSid(1L);
            completionRequest.setSeats(5);
            Long id = ticketingService.reserve(completionRequest, "test1");

            TicketingCancellationRequest dto = new TicketingCancellationRequest();
            dto.setId(id);
            ticketingService.cancel(dto, "test1");

            TicketingCancellationRequest retryingDto = new TicketingCancellationRequest();
            retryingDto.setId(id);

            // when

            // then
            Assertions.assertThatThrownBy(() -> ticketingService.cancel(retryingDto, "test1"))
                    .hasMessage("이미 취소된 내역입니다.");

        }
    }

    @Test
    @DisplayName("이미 관람이 완료된 예매 내역을 취소할 수 없어야 합니다.")
    void cancelAlreadyWatched() {
        Long ticketingId;
        // 예매는 상영 시작 시각 전에.
        try (MockedStatic<LocalDateTime> mock = Mockito.mockStatic(LocalDateTime.class, Mockito.CALLS_REAL_METHODS)) {
            // given
            LocalDateTime now = LocalDateTime.of(2022, 5, 9, 12, 30, 0);
            mock.when(LocalDateTime::now).thenReturn(now);

            TicketingCompletionRequest completionRequest = new TicketingCompletionRequest();
            completionRequest.setSid(2L);
            completionRequest.setSeats(5);
            ticketingId = ticketingService.reserve(completionRequest, "test1");
        }

        // 취소는 상영 시작 시각 이후에.
        try (MockedStatic<LocalDateTime> mock = Mockito.mockStatic(LocalDateTime.class, Mockito.CALLS_REAL_METHODS)) {
            // given
            LocalDateTime now = LocalDateTime.of(2022, 5, 9, 13, 30, 0);
            mock.when(LocalDateTime::now).thenReturn(now);

            TicketingCancellationRequest dto = new TicketingCancellationRequest();
            dto.setId(ticketingId);

            // when

            // then
            Assertions.assertThatThrownBy(() -> ticketingService.cancel(dto, "test1"))
                    .hasMessage("이미 관람이 완료된 내역입니다.");
        }
    }

    @Test
    @DisplayName("본인의 예매 내역을 정상적으로 취소할 수 있어야 합니다.")
    void cancel() {
        try (MockedStatic<LocalDateTime> mock = Mockito.mockStatic(LocalDateTime.class, Mockito.CALLS_REAL_METHODS)) {
            // given
            LocalDateTime now = LocalDateTime.of(2022, 5, 9, 9, 0, 0);
            mock.when(LocalDateTime::now).thenReturn(now);

            TicketingCompletionRequest completionRequest = new TicketingCompletionRequest();
            completionRequest.setSid(1L);
            completionRequest.setSeats(5);
            Long id = ticketingService.reserve(completionRequest, "test1");

            TicketingCancellationRequest dto = new TicketingCancellationRequest();
            dto.setId(id);

            // when
            ticketingService.cancel(dto, "test1");

            // then
            Assertions.assertThat(ticketingService.findById(id).get().getStatus()).isEqualTo(TicketingStatus.C);
        }
    }
}