package com.dongyun.cnucinema.repository;

import com.dongyun.cnucinema.BaseIntegrityTest;
import com.dongyun.cnucinema.dto.CustomerDto;
import com.dongyun.cnucinema.spec.entity.Customer;
import com.dongyun.cnucinema.spec.enums.Sex;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

class DbCustomerRepositoryTest implements BaseIntegrityTest {

    @Autowired DbCustomerRepository customerRepository;

    @Test
    @DisplayName("특정 계정의 정보를 모두 가져와야 합니다.")
    void findOne() {
        // given

        // when
        Customer customer = customerRepository.findByUsername("test1").get();

        // then
        Assertions.assertThat(customer.getUsername()).isEqualTo("test1");
        Assertions.assertThat(customer.getName()).isEqualTo("테스트1");
        Assertions.assertThat(customer.getAuthorities()).contains(new SimpleGrantedAuthority("ADMIN"), new SimpleGrantedAuthority("USER"));
    }

    @Test
    @DisplayName("특정 계정의 만 나이가 정상적으로 계산되어야 합니다.")
    void findOneWithAge() {
        // given

        // when
        Customer customer = customerRepository.findByUsername("test1").get();

        // then
        Assertions.assertThat(customer.getBirthDate()).isEqualTo(LocalDate.parse("2007-10-04"));
        System.out.println(customer.getAge());
        Assertions.assertThat(customer.getAge()).isEqualTo(Period.between(customer.getBirthDate(), LocalDate.now()).getYears());

    }

    @Test
    @DisplayName("Authority가 없는 계정은 Authority에 데이터가 없어야 합니다.")
    void findOneWithoutAuthorities() {
        // given

        // when
        Customer customer = customerRepository.findByUsername("test_wo_authority").get();

        // then
        Assertions.assertThat(customer.getUsername()).isEqualTo("test_wo_authority");
        Assertions.assertThat(customer.getName()).isEqualTo("테스트4");
        Assertions.assertThat(customer.getAuthorities()).hasSize(0);
    }

    @Test
    @DisplayName("여러 계정의 정보가 정상적으로 출력되어야 합니다.")
    void findAll() {
        // given

        // when
        List<Customer> customer = customerRepository.findAll();
        Customer customerWithManyAuthorities = customer.stream().filter(c -> c.getUsername().equals("test1")).findAny().get();
        Customer customerWithOneAuthority = customer.stream().filter(c -> c.getUsername().equals("test2")).findAny().get();
        Customer customerWithoutAuthority = customer.stream().filter(c -> c.getUsername().equals("test_wo_authority")).findAny().get();

        // then
        Assertions.assertThat(customerWithManyAuthorities.getAuthorities()).hasSize(2);
        Assertions.assertThat(customerWithOneAuthority.getAuthorities()).hasSize(1);
        Assertions.assertThat(customerWithoutAuthority.getAuthorities()).hasSize(0);
    }

    @Test
    @DisplayName("새로운 사용자의 정보가 올바르게 추가되어야 합니다.")
    void insert() {
        // given
        Customer customer = Customer.builder()
                .username("test_new_customer")
                .email("test@test.com")
                .birthDate(LocalDate.parse("2020-01-01"))
                .name("테스트사용자")
                .encryptedPassword("not encrypted password")
                .sex(Sex.M)
                .build();

        // when
        customerRepository.save(customer);

        // then
        Assertions.assertThat(customerRepository.findByUsername("test_new_customer")).containsInstanceOf(Customer.class);
    }

    @Test
    @DisplayName("기존 사용자의 정보가 올바르게 수정되어야 합니다.")
    void update() {
        // given
        Customer customer = customerRepository.findByUsername("test1").get();

        // when
        CustomerDto customerDto = CustomerDto.create(customer);
        customerDto.setEmail("new@email.com");
        Customer updatedCustomer = CustomerDto.toEntity(customerDto);
        customerRepository.save(updatedCustomer);

        // then
        customer = customerRepository.findByUsername("test1").get();
        Assertions.assertThat(customer.getEmail()).isEqualTo("new@email.com");
    }
}