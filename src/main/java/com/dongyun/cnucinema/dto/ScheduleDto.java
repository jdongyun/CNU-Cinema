package com.dongyun.cnucinema.dto;

import com.dongyun.cnucinema.spec.entity.Customer;
import com.dongyun.cnucinema.spec.entity.Schedule;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
public class ScheduleDto {

    private Long sid;

    private Long mid;

    private String tname;

    private LocalDateTime showAt;

    public static Schedule toEntity(ScheduleDto s) {
        return Schedule.builder()
                .sid(s.sid)
                .mid(s.mid)
                .tname(s.tname)
                .showAt(s.showAt)
                .build();
    }

    public static ScheduleDto create(Schedule s) {
        return ScheduleDto.builder()
                .sid(s.getSid())
                .mid(s.getMid())
                .tname(s.getTname())
                .showAt(s.getShowAt())
                .build();
    }
}
