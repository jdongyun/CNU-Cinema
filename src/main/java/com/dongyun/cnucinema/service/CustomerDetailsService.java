package com.dongyun.cnucinema.service;

import com.dongyun.cnucinema.spec.entity.Customer;
import com.dongyun.cnucinema.spec.repository.CustomerRepository;
import com.dongyun.cnucinema.userdetails.CustomerDetails;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class CustomerDetailsService implements UserDetailsService {

    private final CustomerRepository customerRepository;

    public CustomerDetailsService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 로그인을 처리하는 비즈니스 로직.
        Customer customer = customerRepository.findByUsername(username)
                .orElseThrow(() -> {
                    throw new UsernameNotFoundException("해당하는 사용자가 없습니다.");
                });

        Collection<GrantedAuthority> authorities = new ArrayList<>(customer.getAuthorities());

        return CustomerDetails.builder()
                .username(customer.getUsername())
                .password(customer.getEncryptedPassword())
                .authorities(authorities)
                .build();
    }
}
