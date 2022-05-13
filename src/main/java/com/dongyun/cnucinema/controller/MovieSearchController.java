package com.dongyun.cnucinema.controller;

import com.dongyun.cnucinema.spec.entity.Actor;
import com.dongyun.cnucinema.spec.entity.Movie;
import com.dongyun.cnucinema.spec.entity.Schedule;
import com.dongyun.cnucinema.spec.service.MovieService;
import com.dongyun.cnucinema.spec.service.ScheduleService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/search")
public class MovieSearchController {
    private final MovieService movieService;
    private final ScheduleService scheduleService;

    public MovieSearchController(MovieService movieService, ScheduleService scheduleService) {
        this.movieService = movieService;
        this.scheduleService = scheduleService;
    }

    @GetMapping("")
    public String searchMovie(
            Model model,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate ticketingDate
    ) {
        List<Movie> movies = new ArrayList<>();
        if (title == null && ticketingDate == null) {
            movies = movieService.findAll();
        } else if (title != null && ticketingDate != null) {
            movies = movieService.findByTitleContainsAndScheduleShowAtDate(title, ticketingDate);
        } else if (title != null) {
            movies = movieService.findByTitleContains(title);
        } else {
            movies = movieService.findByScheduleShowAtDate(ticketingDate);
        }
        model.addAttribute("title", title);
        model.addAttribute("ticketingDate", ticketingDate);
        model.addAttribute("movies", movies);

        return "search/index";
    }

    @GetMapping("/{id}")
    public String movieInfo(@PathVariable Long id, Model model) {
        try {
            Movie movie = movieService.findByMid(id).orElseThrow(() -> {
                throw new IllegalStateException("해당하는 영화 ID가 없습니다.");
            });
            model.addAttribute("title", movie.getTitle());
            model.addAttribute("open_day", movie.getOpenDay());
            model.addAttribute("director", movie.getDirector());
            model.addAttribute("rating", movie.getRating());
            model.addAttribute("length", movie.getLength());
            model.addAttribute("actors", movie.getActors().stream().map(Actor::getName).collect(Collectors.toList()));

            List<Schedule> schedules = scheduleService.findByMid(id);
            model.addAttribute("schedules", schedules);

            return "search/movie_info";
        } catch (IllegalStateException e) {
            model.addAttribute("message", e.getMessage());
            return "error_message";
        }

    }
}