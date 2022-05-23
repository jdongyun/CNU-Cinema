package com.dongyun.cnucinema.service;

import com.dongyun.cnucinema.dto.TicketingCancellationRequest;
import com.dongyun.cnucinema.dto.TicketingCompletionRequest;
import com.dongyun.cnucinema.dto.TicketingDto;
import com.dongyun.cnucinema.spec.entity.Customer;
import com.dongyun.cnucinema.spec.entity.Movie;
import com.dongyun.cnucinema.spec.entity.Schedule;
import com.dongyun.cnucinema.spec.entity.Ticketing;
import com.dongyun.cnucinema.spec.enums.TicketingStatus;
import com.dongyun.cnucinema.spec.repository.CustomerRepository;
import com.dongyun.cnucinema.spec.repository.MovieRepository;
import com.dongyun.cnucinema.spec.repository.ScheduleRepository;
import com.dongyun.cnucinema.spec.repository.TicketingRepository;
import com.dongyun.cnucinema.spec.service.TicketingService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TicketingServiceImpl implements TicketingService {

    private final CustomerRepository customerRepository;
    private final MovieRepository movieRepository;
    private final ScheduleRepository scheduleRepository;
    private final TicketingRepository ticketingRepository;

    public TicketingServiceImpl(CustomerRepository customerRepository, MovieRepository movieRepository, ScheduleRepository scheduleRepository, TicketingRepository ticketingRepository) {
        this.customerRepository = customerRepository;
        this.movieRepository = movieRepository;
        this.scheduleRepository = scheduleRepository;
        this.ticketingRepository = ticketingRepository;
    }

    @Transactional
    @Override
    public Long reserve(TicketingCompletionRequest request, String username) {
        if (request.getSeats() > 10) {
            throw new IllegalStateException("10개의 좌석보다 더 많이 예매할 수 없습니다.");
        }
        Customer customer = customerRepository.findByUsername(username).orElseThrow(() -> {
            throw new IllegalStateException("해당하는 사용자가 없습니다.");
        });
        Schedule schedule = scheduleRepository.findBySid(request.getSid()).orElseThrow(() -> {
            throw new IllegalStateException("해당하는 스케줄이 없습니다.");
        });
        Movie movie = movieRepository.findByMid(schedule.getMid()).orElseThrow(() -> {
            throw new IllegalStateException("해당하는 영화가 없습니다.");
        });
        validateSchedule(schedule);
        validateAge(customer, movie);
        validateRemainSeats(schedule, request.getSeats());

        TicketingDto dto = TicketingDto.builder()
                .sid(schedule.getSid())
                .username(customer.getUsername())
                .rcAt(LocalDateTime.now())
                .seats(request.getSeats())
                .status(TicketingStatus.R)
                .build();

        Ticketing ticketing = TicketingDto.toEntity(dto);
        return ticketingRepository.save(ticketing);
    }

    @Override
    public void cancel(TicketingCancellationRequest request, String username) {
        Ticketing ticketing = ticketingRepository.findById(request.getId()).orElseThrow(() -> {
            throw new IllegalStateException("해당하는 예매 내역이 없습니다.");
        });
        if (!ticketing.getUsername().equals(username)) {
            throw new IllegalStateException("본인의 예매 내역이 아닙니다.");
        }
        if (ticketing.getStatus() == TicketingStatus.C) {
            throw new IllegalStateException("이미 취소된 내역입니다.");
        }
        if (LocalDateTime.now().isAfter(ticketing.getScheduleShowAt())) {
            throw new IllegalStateException("이미 관람이 완료된 내역입니다.");
        }

        TicketingDto dto = TicketingDto.create(ticketing);
        dto.setStatus(TicketingStatus.C);
        dto.setRcAt(LocalDateTime.now());

        ticketingRepository.save(TicketingDto.toEntity(dto));
    }

    @Override
    public Optional<Ticketing> findById(Long id) {
        return ticketingRepository.findById(id);
    }

    @Override
    public List<Ticketing> findBySid(Long sid) {
        return ticketingRepository.findBySid(sid);
    }

    @Override
    public List<Ticketing> findByUsername(String username) {
        return ticketingRepository.findByUsername(username);
    }

    @Override
    public List<Ticketing> findByUsernameAndReserved(String username) {
        return ticketingRepository.findByUsernameAndReservedOrderByRcAtDesc(username);
    }

    @Override
    public List<Ticketing> findByUsernameAndCancelled(String username) {
        return ticketingRepository.findByUsernameAndCancelledOrderByRcAtDesc(username);
    }

    @Override
    public List<Ticketing> findByUsernameAndWatched(String username) {
        return ticketingRepository.findByUsernameAndWatchedOrderByShowAtDesc(username);
    }

    private void validateSchedule(Schedule schedule) {
        if (!schedule.getShowAt().isAfter(LocalDateTime.now())) {
            throw new IllegalStateException("상영 시작 시각이 지나 예매가 불가능합니다.");
        }
    }

    private void validateRemainSeats(Schedule schedule, int seats) {
        if (schedule.getRemainSeats() < seats) {
            throw new IllegalStateException("잔여 좌석보다 예매하고자 하는 좌석의 수가 더 많습니다.");
        }
    }

    private void validateAge(Customer customer, Movie movie) {
        if (movie.getRating().getValue() > customer.getAge()) {
            throw new IllegalStateException("시청 가능 연령이 아닙니다.");
        }
    }
}
