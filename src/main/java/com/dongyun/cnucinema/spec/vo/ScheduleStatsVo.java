package com.dongyun.cnucinema.spec.vo;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Builder
@Getter
@ToString
public class ScheduleStatsVo {

    private LocalDateTime scheduleShowAt;

    private String movieTitle;

    private String customerName;

    private int seats;
}
