package ru.kata.spring.boot_security.demo.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.UserService;
import ru.kata.spring.boot_security.demo.util.UserValidator;

@Controller
public class UserController {
    private static final String ERROR_MESSAGE = "something is wrong";

    private final UserService userService;

    private final UserValidator userValidator;

    @Autowired
    public UserController(UserService userService, UserValidator userValidator) {
        this.userService = userService;
        this.userValidator = userValidator;
    }

    @GetMapping("/users")
    public String printUsers(ModelMap model) {
        model.addAttribute("users", userService.getAllUsers());
        return "user_list";
    }

    @GetMapping("/add_user")
    public String showAddUserPage(ModelMap model) {
        model.addAttribute("user", new User());
        return "add_user";
    }

    @GetMapping("/user")
    public String showUserPage(ModelMap model, @RequestParam("id") int id) {
        model.addAttribute("user", userService.getUserById(id));
        return "user";
    }

    @PostMapping(value = "/add_user", params = "action=cancel")
    public String cancelAdding() {
        return "redirect:/users";
    }

    @PostMapping(value = "/add_user", params = "action=create")
    public String addUser(ModelMap model,
                          @ModelAttribute("user") @Valid User user,
                          BindingResult bindingResult) {
        userValidator.validate(user, bindingResult);
        if (!bindingResult.hasErrors()) {
            userService.saveUser(user);
            return "redirect:/users";
        }
        model.addAttribute("errorMessage", ERROR_MESSAGE);
        return "add_user";
    }

    @PostMapping(value = "/delete_user", params = "id")
    public String deleteUser(@RequestParam int id) {
        userService.removeUserById(id);
        return "redirect:/users";
    }

    @GetMapping(value = "/edit_user", params = "id")
    public String showEditUserPage(ModelMap model, @RequestParam int id) {
        User user = userService.getUserById(id);
        model.addAttribute("user", user);
        model.addAttribute("userId", id);
        return "edit_user";
    }

    @PostMapping(value = "/edit_user", params = "action=cancel")
    public String cancelEditUser() {
        return "redirect:/users";
    }

    @PostMapping(value = "/edit_user", params = "action=update")
    public String editUser(ModelMap model,
                           @ModelAttribute("user") @Valid User user,
                           BindingResult bindingResult) {
        userValidator.validate(user, bindingResult);
        if (!bindingResult.hasErrors()) {
            System.out.println(user);
            userService.updateUser(user);
            return "redirect:/users";
        }
        model.addAttribute("errorMessage", ERROR_MESSAGE);
        return "edit_user";
    }
}
