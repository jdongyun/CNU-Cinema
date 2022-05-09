package com.dongyun.cnucinema.spec.repository;

import com.dongyun.cnucinema.spec.entity.Customer;

import java.util.List;
import java.util.Optional;

public interface CustomerRepository {
    Optional<Customer> findByUsername(String username);

    List<Customer> findAll();

    void save(Customer customer);
}
