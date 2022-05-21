package com.dongyun.cnucinema.spec.repository;

import com.dongyun.cnucinema.spec.entity.Ticketing;

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
}
