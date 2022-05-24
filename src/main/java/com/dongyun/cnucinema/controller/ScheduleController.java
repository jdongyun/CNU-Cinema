package com.dongyun.cnucinema.controller;

import com.dongyun.cnucinema.dto.ScheduleCreateRequest;
import com.dongyun.cnucinema.spec.entity.Movie;
import com.dongyun.cnucinema.spec.service.MovieService;
import com.dongyun.cnucinema.spec.service.ScheduleService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/schedule")
public class ScheduleController {

    private final MovieService movieService;
    private final ScheduleService scheduleService;

    public ScheduleController(MovieService movieService, ScheduleService scheduleService) {
        this.movieService = movieService;
        this.scheduleService = scheduleService;
    }

    @GetMapping("")
    public String retrieve(Model model) {
        return "schedule/index";
    }

    @PostMapping("")
    public String create(Model model, @Validated ScheduleCreateRequest request) {
        try {
            Movie movie = movieService.findByMid(request.getMid()).orElseThrow(() -> {
                throw new IllegalStateException("movie ID가 없습니다.");
            });

            scheduleService.create(request, movie);
        } catch (IllegalStateException e) {
            model.addAttribute("message", e.getMessage());
            return "error_message";
        }

        return "redirect:";
    }
}
