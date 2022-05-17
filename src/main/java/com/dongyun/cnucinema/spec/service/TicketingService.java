package com.dongyun.cnucinema.spec.service;

import com.dongyun.cnucinema.dto.TicketingCompletionRequest;
import com.dongyun.cnucinema.spec.entity.Ticketing;

import java.util.List;

public interface TicketingService {
    Long reserve(TicketingCompletionRequest request, String username);
    // TODO: 예약 취소 기능 구현.
    List<Ticketing> findByUsername(String username);
}
