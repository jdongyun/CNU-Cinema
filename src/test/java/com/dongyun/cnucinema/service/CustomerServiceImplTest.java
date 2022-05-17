package com.dongyun.cnucinema.service;

import com.dongyun.cnucinema.BaseIntegrityTest;
import com.dongyun.cnucinema.dto.CustomerJoinRequest;
import com.dongyun.cnucinema.spec.entity.Customer;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.annotation.DirtiesContext;

import static org.springframework.test.annotation.DirtiesContext.MethodMode.BEFORE_METHOD;

class CustomerServiceImplTest implements BaseIntegrityTest {

    @Autowired CustomerServiceImpl customerService;

    @Test
    @DisplayName("새로운 사용자가 정상적으로 가입되어야 합니다.")
    void join() {
        // given
        CustomerJoinRequest dto = CustomerJoinRequest.builder()
                .username("test_new_customer")
                .name("테스트사용자")
                .email("test@email.com")
                .password("not encrypted password")
                .birthDate("2020-01-01")
                .sex("M")
                .build();

        // when
        customerService.join(dto);

        // then
        Customer customer = customerService.findOne("test_new_customer").get();
        Assertions.assertThat(customer.getUsername()).isEqualTo("test_new_customer");
        Assertions.assertThat(customer.getAuthorities()).contains(new SimpleGrantedAuthority("USER"));
        Assertions.assertThat(customer.getAuthorities()).doesNotContain(new SimpleGrantedAuthority("ADMIN"));
    }

    @Test
    @DisplayName("중복된 사용자 ID가 있으면 가입이 불가능해야 합니다.")
    void joinDuplicate() {
        // given

        // when
        CustomerJoinRequest dto = CustomerJoinRequest.builder()
                .username("test1")
                .name("중복된사용자")
                .email("test@email.com")
                .password("not encrypted password")
                .birthDate("2020-01-01")
                .sex("M")
                .build();

        // then
        Assertions.assertThatThrownBy(() -> customerService.join(dto))
                .hasMessageContaining("이미 존재하는 ID입니다.");
    }
}