package com.dongyun.cnucinema.spec.service;

import com.dongyun.cnucinema.dto.TicketingCancellationRequest;
import com.dongyun.cnucinema.dto.TicketingCompletionRequest;
import com.dongyun.cnucinema.spec.entity.Ticketing;
import com.dongyun.cnucinema.spec.enums.TicketingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TicketingService {
    Long reserve(TicketingCompletionRequest request, String username);

    void cancel(TicketingCancellationRequest request, String username);

    Optional<Ticketing> findById(Long id);

    List<Ticketing> findBySid(Long sid);

    List<Ticketing> findByUsername(String username);

    List<Ticketing> findByUsernameAndReserved(String username);

    List<Ticketing> findByUsernameAndCancelled(String username);

    List<Ticketing> findByUsernameAndWatched(String username);

    List<Ticketing> findByUsernameAndReservedAndRcAtBetween(String username, LocalDateTime startAt, LocalDateTime endAt);

    List<Ticketing> findByUsernameAndCancelledAndRcAtBetween(String username, LocalDateTime startAt, LocalDateTime endAt);

    List<Ticketing> findByUsernameAndWatchedAndRcAtBetween(String username, LocalDateTime startAt, LocalDateTime endAt);
}
