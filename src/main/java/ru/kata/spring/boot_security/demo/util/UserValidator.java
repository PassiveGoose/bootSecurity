package ru.kata.spring.boot_security.demo.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import ru.kata.spring.boot_security.demo.model.User;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.util.Optional;

@Controller
public class UserValidator implements Validator {

    private final UserService userService;

    @Autowired
    public UserValidator(final UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return User.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        User user = (User) target;
        Optional<User> userInDB = userService.getUserByUsername(user.getUsername());

        if (userInDB.isPresent() && userInDB.get().getId() != user.getId()) {
            errors.rejectValue("username", "", "Username is already in db");
        }
    }
}
