package com.dongyun.cnucinema.controller;

import com.dongyun.cnucinema.dto.TicketingCompletionRequest;
import com.dongyun.cnucinema.dto.TicketingProcessingRequest;
import com.dongyun.cnucinema.spec.entity.Movie;
import com.dongyun.cnucinema.spec.entity.Schedule;
import com.dongyun.cnucinema.spec.service.MovieService;
import com.dongyun.cnucinema.spec.service.ScheduleService;
import com.dongyun.cnucinema.spec.service.TicketingService;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Arrays;

@Controller
@RequestMapping("/ticketing")
public class TicketingController {
    private final ScheduleService scheduleService;

    private final MovieService movieService;

    private final TicketingService ticketingService;

    public TicketingController(ScheduleService scheduleService, MovieService movieService, TicketingService ticketingService) {
        this.scheduleService = scheduleService;
        this.movieService = movieService;
        this.ticketingService = ticketingService;
    }

    @PostMapping("/process")
    public String processReservation(Model model, @Validated TicketingProcessingRequest request, BindingResult result) {
        if (result.hasErrors()) {
            String errors = Arrays.toString(result.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).toArray());
            model.addAttribute("message", errors);
            return "error_message";
        }

        try {
            Schedule schedule = scheduleService.findBySid(request.getSid())
                    .orElseThrow(() -> {
                        throw new IllegalStateException("해당하는 스케줄이 없습니다.");
                    }
            );
            Movie movie = movieService.findByMid(schedule.getMid())
                    .orElseThrow(() -> {
                        throw new IllegalStateException("해당하는 영화가 없습니다.");
                    });
            model.addAttribute("sid", schedule.getSid());
            model.addAttribute("movie_title", movie.getTitle());
            model.addAttribute("show_at", schedule.getShowAt());
            model.addAttribute("tname", schedule.getTname());
            model.addAttribute("remain_seats", schedule.getRemainSeats());
        } catch (IllegalStateException e) {
            model.addAttribute("message", e.getMessage());
            return "error_message";
        }

        return "ticketing/reservation_process";
    }

    @PostMapping("/completion")
    public String completeReservation(Authentication auth, Model model, @Validated TicketingCompletionRequest request, BindingResult result) {
        if (result.hasErrors()) {
            String errors = Arrays.toString(result.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).toArray());
            model.addAttribute("message", errors);
            return "error_message";
        }

        try {
            ticketingService.reserve(request, auth.getName());
            Schedule schedule = scheduleService.findBySid(request.getSid()).orElse(Schedule.builder().build());
            Movie movie = movieService.findByMid(schedule.getMid()).orElse(Movie.builder().build());

            model.addAttribute("movie_title", movie.getTitle());
            model.addAttribute("show_at", schedule.getShowAt());
            model.addAttribute("tname", schedule.getTname());
            model.addAttribute("reserved_seats", request.getSeats());
        } catch (IllegalStateException e) {
            model.addAttribute("message", e.getMessage());
            return "error_message";
        }

        return "ticketing/reservation_completion";
    }
}
