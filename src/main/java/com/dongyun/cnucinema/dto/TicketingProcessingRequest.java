package com.dongyun.cnucinema.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
public class TicketingProcessingRequest {

    @NotNull
    private Long sid;
}
