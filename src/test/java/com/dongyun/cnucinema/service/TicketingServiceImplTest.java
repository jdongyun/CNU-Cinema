package com.dongyun.cnucinema.service;

import com.dongyun.cnucinema.BaseIntegrityTest;
import com.dongyun.cnucinema.dto.CustomerDto;
import com.dongyun.cnucinema.dto.TicketingCompletionRequest;
import com.dongyun.cnucinema.spec.entity.Customer;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;

class TicketingServiceImplTest implements BaseIntegrityTest {

    @Autowired TicketingServiceImpl ticketingService;

    @Autowired CustomerServiceImpl customerService;

    @Test
    @DisplayName("10자리를 초과하는 좌석은 티켓팅이 불가능하여야 합니다.")
    void maximumSeats() {
        // given
        TicketingCompletionRequest request = new TicketingCompletionRequest();
        request.setSid(1L);
        request.setSeats(11);

        // when

        // then
        Assertions.assertThatThrownBy(() ->
                ticketingService.reserve(request, "test1"))
                .hasMessageContaining("좌석");
    }

    @Test
    @DisplayName("사용자의 연령보다 시청 가능 연령이 높은 영화는 티켓팅이 불가능하여야 합니다.")
    void minimumAge() {
        // given
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