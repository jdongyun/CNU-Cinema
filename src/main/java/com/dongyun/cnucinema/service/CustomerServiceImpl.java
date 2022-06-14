package com.dongyun.cnucinema.service;

import com.dongyun.cnucinema.dto.CustomerJoinRequest;
import com.dongyun.cnucinema.dto.CustomerDto;
import com.dongyun.cnucinema.spec.entity.Customer;
import com.dongyun.cnucinema.spec.entity.Authority;
import com.dongyun.cnucinema.spec.enums.Sex;
import com.dongyun.cnucinema.spec.repository.CustomerRepository;
import com.dongyun.cnucinema.spec.service.CustomerService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    private final PasswordEncoder passwordEncoder;

    public CustomerServiceImpl(CustomerRepository customerRepository, PasswordEncoder passwordEncoder) {
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public boolean join(CustomerJoinRequest dto) {
        // 가입을 처리하는 비즈니스 로직.
        Set<GrantedAuthority> roles = new HashSet<>(List.of(new SimpleGrantedAuthority("USER")));

        validateDuplicateId(dto.getUsername());

        String encryptedPassword = passwordEncoder.encode(dto.getPassword());

        CustomerDto customer = CustomerDto.builder()
                .username(dto.getUsername())
                .name(dto.getName())
                .email(dto.getEmail())
                .password(encryptedPassword)
                .birthDate(LocalDate.parse(dto.getBirthDate()))
                .sex(Sex.valueOf(dto.getSex()))
                .authorities(roles)
                .build();

        customerRepository.save(CustomerDto.toEntity(customer));
        return true;
    }

    @Override
    public Optional<Customer> findOne(String username) {
        return customerRepository.findByUsername(username);
    }

    @Override
    public List<Customer> findAll() {
        return customerRepository.findAll();
    }

    @Override
    public void save(CustomerDto dto) {
        customerRepository.save(CustomerDto.toEntity(dto));
    }

    private void validateDuplicateId(String username) {
        customerRepository.findByUsername(username)
                        .ifPresent(c -> {
                            throw new IllegalStateException("이미 존재하는 ID입니다.");
                        });
    }
}
