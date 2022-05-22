package com.dongyun.cnucinema.controller;

import com.dongyun.cnucinema.spec.enums.TicketingStatus;
import com.dongyun.cnucinema.spec.service.TicketingService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Arrays;

@Controller
@RequestMapping("/user")
public class CustomerController {

    private final TicketingService ticketingService;

    public CustomerController(TicketingService ticketingService) {
        this.ticketingService = ticketingService;
    }

    @GetMapping("")
    public String home(Authentication auth, Model model) {
        String username = auth.getName();
        model.addAttribute("username", username);
        model.addAttribute("authorities", Arrays.toString(auth.getAuthorities().toArray()));

        model.addAttribute("reservedList", ticketingService.findByUsernameAndReserved(username));
        model.addAttribute("cancelledList", ticketingService.findByUsernameAndCancelled(username));
        model.addAttribute("watchedList", ticketingService.findByUsernameAndWatched(username));

        return "user/index";
    }
}
