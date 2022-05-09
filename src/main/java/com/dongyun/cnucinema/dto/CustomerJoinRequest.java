package com.dongyun.cnucinema.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.*;

@Builder
@Getter
@Setter
@ToString
public class CustomerJoinRequest {
    @NotNull
    @Size(max = 20)
    private String username;

    @NotNull
    @Size(max = 10)
    private String name;

    @NotNull
    private String password;

    @NotNull
    @Email
    @Size(max = 30)
    private String email;

    @NotNull
    private String birthDate;

    @NotNull
    @Pattern(regexp = "^[MF]$", message = "Type of sex is not valid")
    private String sex;
}
