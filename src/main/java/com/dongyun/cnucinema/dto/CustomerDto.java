package com.dongyun.cnucinema.dto;

import com.dongyun.cnucinema.spec.entity.Customer;
import com.dongyun.cnucinema.spec.enums.Sex;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.Set;

@Builder
@Getter
@Setter
public class CustomerDto {
    @NotNull
    @Size(max = 20)
    private String username;

    @NotNull
    @Size(max = 10)
    private String name;

    @NotNull
    @Size(max = 500)
    private String password;

    @NotNull
    @Email
    @Size(max = 30)
    private String email;

    @NotNull
    private LocalDate birthDate;

    @NotNull
    @Pattern(regexp = "^[MF]$", message = "Type of sex is not valid")
    private Sex sex;

    private Set<GrantedAuthority> authorities;

    public static Customer toEntity(CustomerDto c) {
        return Customer.builder()
                .username(c.username)
                .name(c.name)
                .encryptedPassword(c.password)
                .email(c.email)
                .birthDate(c.birthDate)
                .sex(c.sex)
                .authorities(c.authorities)
                .build();
    }

    public static CustomerDto create(Customer c) {
        return CustomerDto.builder()
                .username(c.getUsername())
                .name(c.getName())
                .password(c.getEncryptedPassword())
                .email(c.getEmail())
                .birthDate(c.getBirthDate())
                .sex(c.getSex())
                .authorities(c.getAuthorities())
                .build();
    }
}
