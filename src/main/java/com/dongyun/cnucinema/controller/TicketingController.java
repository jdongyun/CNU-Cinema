package com.dongyun.cnucinema.controller;

import com.dongyun.cnucinema.dto.TicketingCancellationRequest;
import com.dongyun.cnucinema.dto.TicketingCompletionRequest;
import com.dongyun.cnucinema.dto.TicketingProcessingRequest;
import com.dongyun.cnucinema.spec.entity.Customer;
import com.dongyun.cnucinema.spec.entity.Movie;
import com.dongyun.cnucinema.spec.entity.Schedule;
import com.dongyun.cnucinema.spec.entity.Ticketing;
import com.dongyun.cnucinema.spec.service.*;
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

    private final CustomerService customerService;

    private final ScheduleService scheduleService;

    private final MovieService movieService;

    private final TicketingService ticketingService;

    private final MailService mailService;

    public TicketingController(CustomerService customerService, ScheduleService scheduleService, MovieService movieService, TicketingService ticketingService, MailService mailService) {
        this.customerService = customerService;
        this.scheduleService = scheduleService;
        this.movieService = movieService;
        this.ticketingService = ticketingService;
        this.mailService = mailService;
    }

    @PostMapping("/process")
    public String processReservation(Model model, @Validated TicketingProcessingRequest request, BindingResult result) {
        if (result.hasErrors()) {
            String errors = Arrays.toString(result.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).toArray());
            model.addAttribute("message", errors);
            return "error_message";
        }

        try {
            // 스케줄과 영화 정보를 각각 불러온다.
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
            // Request에 포함된 Schedule ID와 예매할 좌석 수를 받아와 예매 처리한다.
            Long id = ticketingService.reserve(request, auth.getName());

            Ticketing ticketing = ticketingService.findById(id).orElseThrow(() -> {
                throw new IllegalStateException("예매가 실패하였습니다.");
            });
            Schedule schedule = scheduleService.findBySid(request.getSid()).orElse(Schedule.builder().build());
            Customer customer = customerService.findOne(auth.getName()).orElseThrow(() -> {
                throw new IllegalStateException("해당하는 사용자가 없습니다.");
            });

            model.addAttribute("movie_title", ticketing.getMovieTitle());
            model.addAttribute("show_at", schedule.getShowAt());
            model.addAttribute("tname", schedule.getTname());
            model.addAttribute("reserved_seats", ticketing.getSeats());

            new Thread(() -> {
                // 새로운 쓰레드에서 메일을 전송한다.
                mailService.sendTicketingMail(customer, schedule, ticketing);
            }).start();
        } catch (IllegalStateException e) {
            model.addAttribute("message", e.getMessage());
            return "error_message";
        }

        return "ticketing/reservation_completion";
    }

    @PostMapping("/cancellation")
    public String cancelReservation(Authentication auth, Model model, @Validated TicketingCancellationRequest request, BindingResult result) {
        if (result.hasErrors()) {
            String errors = Arrays.toString(result.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).toArray());
            model.addAttribute("message", errors);
            return "error_message";
        }

        try {
            ticketingService.cancel(request, auth.getName());
        } catch (IllegalStateException e) {
            model.addAttribute("message", e.getMessage());
            return "error_message";
        }

        return "ticketing/reservation_cancellation";
    }
}
