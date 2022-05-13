package com.dongyun.cnucinema.spec.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Builder
@Getter
@ToString
public class Schedule {
    private Long sid;

    private LocalDateTime showAt;

    private String tname;

    private Long mid;

    private int remainSeats;
}
