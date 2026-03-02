package com.ait.shop.controller;

import com.ait.shop.dto.user.UserRegistrationDto;
import com.ait.shop.service.interfaces.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @PostMapping("/register")
    public String register(@RequestBody UserRegistrationDto registrationDto) {
        service.register(registrationDto);
        return "Registration complete. Please check your email";
    }

    @GetMapping("/confirm/{codeValue}")
    public String confirm(@PathVariable String codeValue) {
        service.confirm(codeValue);
        return "Your email has been successfully confirmed";
    }
}
