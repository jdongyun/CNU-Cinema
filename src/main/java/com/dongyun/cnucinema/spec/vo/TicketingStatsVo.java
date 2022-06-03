package com.dongyun.cnucinema.spec.vo;

import com.dongyun.cnucinema.spec.enums.TicketingStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Builder
@Getter
@ToString
public class TicketingStatsVo {

    private String customerName;

    private LocalDateTime rcAt;

    private TicketingStatus status;

    private int seats;
}
