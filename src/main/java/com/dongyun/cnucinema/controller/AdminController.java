package com.dongyun.cnucinema.controller;

import com.dongyun.cnucinema.spec.service.MovieService;
import com.dongyun.cnucinema.spec.service.ScheduleService;
import com.dongyun.cnucinema.spec.service.TicketingService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final MovieService movieService;

    private final ScheduleService scheduleService;

    private final TicketingService ticketingService;

    public AdminController(MovieService movieService, ScheduleService scheduleService, TicketingService ticketingService) {
        this.movieService = movieService;
        this.scheduleService = scheduleService;
        this.ticketingService = ticketingService;
    }

    @GetMapping("")
    public String home(Model model,
                       RedirectAttributes re,
                       @RequestParam(required = false, name = "start_date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                       @RequestParam(required = false, name = "end_date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        // 관리자 페이지 컨트롤러.
        if (startDate == null || endDate == null || startDate.isAfter(endDate)) {
            // 날짜가 입력되어 있지 않거나 시작-종료일자가 반대로 되어 있으면 초기화한다.
            re.addAttribute("start_date", LocalDate.now().minusDays(30).toString());
            re.addAttribute("end_date", LocalDate.now().toString());
            return "redirect:/admin";
        }
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);

        model.addAttribute("movieStats", movieService.findByRcAtBetweenWithRank(startDate, endDate));
        model.addAttribute("scheduleStats", scheduleService.findStatsByRcAtBetween(startDate, endDate));
        model.addAttribute("ticketingStats", ticketingService.findTicketingStatsByRcAtBetween(startDate, endDate));

        return "admin/index";
    }
}
