package com.dongyun.cnucinema.spec.service;

import com.dongyun.cnucinema.dto.CustomerJoinRequest;
import com.dongyun.cnucinema.dto.CustomerDto;
import com.dongyun.cnucinema.spec.entity.Customer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface CustomerService {
    boolean join(CustomerJoinRequest dto);

    Optional<Customer> findOne(String username);

    List<Customer> findAll();

    void save(CustomerDto dto);
}
