package com.dongyun.cnucinema.spec.repository;

import com.dongyun.cnucinema.spec.entity.Ticketing;

import java.util.List;
import java.util.Optional;

public interface TicketingRepository {
    Long save(Ticketing ticketing);

    Optional<Ticketing> findById(Long id);

    List<Ticketing> findBySid(Long sid);

    List<Ticketing> findByUsername(String username);
}
