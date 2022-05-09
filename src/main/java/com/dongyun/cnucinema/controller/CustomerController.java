package com.dongyun.cnucinema.controller;

import com.dongyun.cnucinema.userdetails.CustomerDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.Arrays;

@Controller
@RequestMapping("/user")
public class CustomerController {

    @GetMapping("")
    public String home(Authentication auth, Model model) {
        model.addAttribute("username", auth.getName());
        model.addAttribute("authorities", Arrays.toString(auth.getAuthorities().toArray()));

        return "user/index";
    }
}
