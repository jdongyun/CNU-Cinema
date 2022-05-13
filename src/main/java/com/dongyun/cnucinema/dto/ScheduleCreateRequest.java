package com.dongyun.cnucinema.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Builder
@Getter
@Setter
@ToString
public class ScheduleCreateRequest {
    @NotNull
    private Long mid;

    @NotNull
    @Size(min = 1, max = 20)
    private String tname;

    @NotNull
    private String showAt;
}
