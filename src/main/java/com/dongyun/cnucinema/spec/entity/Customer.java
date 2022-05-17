package com.dongyun.cnucinema.spec.entity;

import com.dongyun.cnucinema.spec.enums.Sex;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.Set;

@Builder
@Getter
public class Customer {

    private String username;

    private String name;

    private String encryptedPassword;

    private String email;

    private LocalDate birthDate;

    private Sex sex;

    private Set<GrantedAuthority> authorities;

    public int getAge() {
        return Period.between(this.getBirthDate(), LocalDate.now()).getYears();
    }
}
