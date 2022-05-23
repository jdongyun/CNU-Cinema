package com.dongyun.cnucinema.controller;

import com.dongyun.cnucinema.spec.service.TicketingService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;

@Controller
@RequestMapping("/user")
public class CustomerController {

    private final TicketingService ticketingService;

    public CustomerController(TicketingService ticketingService) {
        this.ticketingService = ticketingService;
    }

    @GetMapping("")
    public String home(
            Authentication auth,
            Model model,
            RedirectAttributes re,
            @RequestParam(required = false, name = "start_date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false, name = "end_date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {

        String username = auth.getName();
        model.addAttribute("username", username);
        model.addAttribute("authorities", Arrays.toString(auth.getAuthorities().toArray()));

        if (startDate == null || endDate == null || startDate.isAfter(endDate)) {
            re.addAttribute("start_date", LocalDate.now().minusDays(30).toString());
            re.addAttribute("end_date", LocalDate.now().toString());
            return "redirect:/user";
        }

        LocalDateTime startAt = LocalDateTime.of(startDate, LocalTime.MIDNIGHT);
        LocalDateTime endAt = LocalDateTime.of(endDate.plusDays(1), LocalTime.MIDNIGHT).minusSeconds(1);

        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);

        model.addAttribute("reservedList",
                ticketingService.findByUsernameAndReservedAndRcAtBetween(username, startAt, endAt));
        model.addAttribute("cancelledList",
                ticketingService.findByUsernameAndCancelledAndRcAtBetween(username, startAt, endAt));
        model.addAttribute("watchedList",
                ticketingService.findByUsernameAndWatchedAndRcAtBetween(username, startAt, endAt));

        return "user/index";
    }
}
