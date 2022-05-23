package com.dongyun.cnucinema.spec.repository;

import com.dongyun.cnucinema.spec.entity.Ticketing;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TicketingRepository {
    Long save(Ticketing ticketing);

    Optional<Ticketing> findById(Long id);

    List<Ticketing> findBySid(Long sid);

    List<Ticketing> findByUsername(String username);

    List<Ticketing> findByUsernameAndReservedOrderByRcAtDesc(String username);

    List<Ticketing> findByUsernameAndCancelledOrderByRcAtDesc(String username);

    List<Ticketing> findByUsernameAndWatchedOrderByShowAtDesc(String username);

    List<Ticketing> findByUsernameAndReservedAndRcAtBetweenOrderByRcAtDesc(String username, LocalDateTime startAt, LocalDateTime endAt);

    List<Ticketing> findByUsernameAndCancelledAndRcAtBetweenOrderByRcAtDesc(String username, LocalDateTime startAt, LocalDateTime endAt);

    List<Ticketing> findByUsernameAndWatchedAndRcAtBetweenOrderByShowAtDesc(String username, LocalDateTime startAt, LocalDateTime endAt);
}
