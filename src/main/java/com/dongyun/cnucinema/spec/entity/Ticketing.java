package com.dongyun.cnucinema.spec.entity;

import com.dongyun.cnucinema.spec.enums.TicketingStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;


@Builder
@Getter
@ToString
public class Ticketing {

    private Long id;

    private Long sid;

    private String username;

    private LocalDateTime rcAt;

    private int seats;

    private TicketingStatus status;

    private String movieTitle;

    private LocalDateTime scheduleShowAt;
}
