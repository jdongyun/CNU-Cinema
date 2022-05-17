package com.dongyun.cnucinema.service;

import com.dongyun.cnucinema.BaseIntegrityTest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

class CustomerDetailsServiceTest implements BaseIntegrityTest {

    @Autowired CustomerDetailsService customerDetailsService;

    @Test
    @DisplayName("존재하는 사용자 ID로 로그인 계정을 찾아야 합니다.")
    void loadUserByExistingUsername() {
        // given

        // when
        UserDetails userDetails = customerDetailsService.loadUserByUsername("test1");

        // then
        Assertions.assertThat(userDetails.getUsername()).isEqualTo("test1");
    }

    @Test
    @DisplayName("존재하지 않는 사용자 ID로는 계정을 찾을 수 없어야 합니다.")
    void findByNonExistingUsername() {
        // given

        // when

        // then
        Assertions.assertThatThrownBy(() -> customerDetailsService.loadUserByUsername("user_not_found"))
                .isInstanceOf(UsernameNotFoundException.class);
    }

}