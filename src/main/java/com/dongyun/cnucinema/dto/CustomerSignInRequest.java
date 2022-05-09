package com.dongyun.cnucinema.dto;

import lombok.Getter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
public class CustomerSignInRequest {
    @NotNull
    @Size(max = 20)
    private String username;

    @NotNull
    @Size(max = 50)
    private String password;
}
