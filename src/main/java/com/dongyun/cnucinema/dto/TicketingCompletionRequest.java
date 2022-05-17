package com.dongyun.cnucinema.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
public class TicketingCompletionRequest {

    @NotNull
    private Long sid;

    @NotNull
    @Min(1)
    @Max(10)
    private int seats;
}
