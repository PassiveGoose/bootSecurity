package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.UserService;
import ru.kata.spring.boot_security.demo.util.UserValidator;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private final UserValidator userValidator;

    private final UserService userService;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthController(UserValidator userValidator,
                          UserService userService,
                          PasswordEncoder passwordEncoder) {
        this.userValidator = userValidator;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";
    }

    @GetMapping("/register")
    public String showRegisterPage(@ModelAttribute("user") User user) {
        return "auth/register";
    }

    @PostMapping(value = "/register", params = "action=cancel")
    public String cancelRegister() {
        return "redirect:login";
    }

    @PostMapping(value = "/register", params = "action=create")
    public String register(@ModelAttribute("user") User user,
                           BindingResult bindingResult) {
        userValidator.validate(user, bindingResult);
        if (bindingResult.hasErrors()) {
            return "redirect:register?error";
        }
        user.addRole(userService.getRoleByName("USER").orElseGet(() -> new Role("USER")));
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userService.saveUser(user);
        return "redirect:login";
    }
}
