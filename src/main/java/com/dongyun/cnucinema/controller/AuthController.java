package com.dongyun.cnucinema.controller;

import com.dongyun.cnucinema.dto.CustomerJoinRequest;
import com.dongyun.cnucinema.spec.service.CustomerService;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private final CustomerService customerService;

    public AuthController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping("/signup")
    public String createUser(Model model, @Validated CustomerJoinRequest request, BindingResult bindingResult) {
        // 회원가입 페이지 컨트롤러.
        if (bindingResult.hasErrors()) {
            String errors = Arrays.toString(bindingResult.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).toArray());
            model.addAttribute("message", errors);
            return "error_message";
        }

        try {
            customerService.join(request);
        } catch (IllegalStateException e) {
            model.addAttribute("message", e.getMessage());
            System.out.println(e.getMessage());
            return "error_message";
        }
        return "redirect:/login";
    }

    @GetMapping("/signup")
    public String signUpPage(Model model) {
        return "signup";
    }
}
