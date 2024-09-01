package ru.kata.spring.boot_security.demo.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.UserService;
import ru.kata.spring.boot_security.demo.util.UserValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;

    private final UserValidator userValidator;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AdminController(UserService userService, UserValidator userValidator, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.userValidator = userValidator;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/users")
    public String printUsers(ModelMap model) {
        model.addAttribute("users", userService.getAllUsers());
        return "admin/user_list";
    }

    @GetMapping("/add_user")
    public String showAddUserPage(ModelMap model) {
        List<Role> roles = new ArrayList<>();
        roles.add(new Role("USER"));
        roles.add(new Role("ADMIN"));

        model.addAttribute("user", new User());
        model.addAttribute("roles", roles);
        return "admin/add_user";
    }

    @PostMapping(value = "/add_user", params = "action=cancel")
    public String cancelAdding() {
        return "redirect:/admin/users";
    }

    @PostMapping(value = "/add_user", params = "action=create")
    public String addUser(@ModelAttribute("user") @Valid User user,
                          @RequestParam("roles") List<String> roles,
                          BindingResult bindingResult) {
        setRolesToUser(user, roles);
        userValidator.validate(user, bindingResult);
        if (!bindingResult.hasErrors()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            userService.saveUser(user);
            return "redirect:/admin/users";
        }
        return "redirect:add_user?error";
    }

    @PostMapping(value = "/delete_user", params = "id")
    public String deleteUser(@RequestParam int id) {
        userService.removeUserById(id);
        return "redirect:/admin/users";
    }

    @GetMapping(value = "/edit_user", params = "id")
    public String showEditUserPage(ModelMap model, @RequestParam int id) {
        List<Role> roles = new ArrayList<>();
        roles.add(new Role());
        roles.add(new Role("ADMIN"));

        User user = userService.getUserById(id);
        model.addAttribute("user", user);
        model.addAttribute("userId", id);
        model.addAttribute("roles", roles);
        return "admin/edit_user";
    }

    @PostMapping(value = "/edit_user", params = "action=cancel")
    public String cancelEditUser() {
        return "redirect:/admin/users";
    }

    @PostMapping(value = "/edit_user", params = "action=update")
    public String editUser(@ModelAttribute("user") @Valid User user,
                           @RequestParam("roles") List<String> roles,
                           BindingResult bindingResult) {
        setRolesToUser(user, roles);
        userValidator.validate(user, bindingResult);
        if (!bindingResult.hasErrors()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            userService.updateUser(user);
            return "redirect:/admin/users";
        }
        return "redirect:edit_user?error&id=" + user.getId();
    }

    private void setRolesToUser(User user, List<String> roles) {
        List<Role> rolesForUser = new ArrayList<>();
        for (String role : roles) {
            Optional<Role> roleOpt = userService.getRoleByName(role);
            Role roleForUser = roleOpt.orElseGet(() -> new Role(role));
            rolesForUser.add(roleForUser);
        }
        user.setRoles(rolesForUser);
    }
}
