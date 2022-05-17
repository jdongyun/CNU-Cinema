package com.dongyun.cnucinema.dto;

import com.dongyun.cnucinema.spec.entity.Customer;
import com.dongyun.cnucinema.spec.entity.Ticketing;
import com.dongyun.cnucinema.spec.enums.TicketingStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
public class TicketingDto {

    private Long id;

    private Long sid;

    private String username;

    private LocalDateTime rcAt;

    private int seats;

    private TicketingStatus status;

    public static Ticketing toEntity(TicketingDto t) {
        return Ticketing.builder()
                .id(t.id)
                .sid(t.sid)
                .username(t.username)
                .rcAt(t.rcAt)
                .seats(t.seats)
                .status(t.status)
                .build();
    }

    public static TicketingDto create(Ticketing t) {
        return TicketingDto.builder()
                .id(t.getId())
                .sid(t.getSid())
                .username(t.getUsername())
                .rcAt(t.getRcAt())
                .seats(t.getSeats())
                .status(t.getStatus())
                .build();
    }
}
