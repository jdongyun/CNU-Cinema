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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        // 영화 검색 컨트롤러.
        List<Movie> movies = new ArrayList<>();
        if (title == null && ticketingDate == null) {
            // 영화 제목과 관람 일자를 모두 받지 않으면 전체 영화 정보를 보여준다.
            movies = movieService.findAll();
        } else if (title != null && ticketingDate != null) {
            movies = movieService.findByTitleContainsAndScheduleShowAtDate(title, ticketingDate);
        } else if (title != null) {
            movies = movieService.findByTitleContains(title);
        } else {
            movies = movieService.findByScheduleShowAtDate(ticketingDate);
        }

        // 현재 상영 중인 영화와 개봉 예정인 영화를 분리한다.
        List<Movie> nowOpenMovies = movies.stream().filter(m -> !m.getOpenDay().isAfter(LocalDate.now())).toList();
        List<Movie> openExpectedMovies = movies.stream().filter(m -> m.getOpenDay().isAfter(LocalDate.now())).toList();

        model.addAttribute("title", title);
        model.addAttribute("ticketingDate", ticketingDate);
        model.addAttribute("nowOpenMovies", nowOpenMovies);
        model.addAttribute("openExpectedMovies", openExpectedMovies);

        return "search/index";
    }

    @GetMapping("/{id}")
    public String movieInfo(@PathVariable Long id, Model model) {
        // 영화 별 정보 및 예매 컨트롤러.
        try {
            Movie movie = movieService.findByMid(id).orElseThrow(() -> {
                throw new IllegalStateException("해당하는 영화 ID가 없습니다.");
            });
            // 영화 ID에 해당하는 스케줄을 모두 불러온다.
            List<Schedule> schedules = scheduleService.findByMid(id);

            // 영화관 별로 분리하여 Map에 저장한다.
            Map<String, List<Schedule>> schedulesByTname = schedules.stream().collect(Collectors.groupingBy(Schedule::getTname));

            model.addAttribute("movie", movie);
            model.addAttribute("schedules", schedulesByTname);
            model.addAttribute("now", LocalDate.now());

            return "search/movie_info";
        } catch (IllegalStateException e) {
            model.addAttribute("message", e.getMessage());
            return "error_message";
        }

    }
}
