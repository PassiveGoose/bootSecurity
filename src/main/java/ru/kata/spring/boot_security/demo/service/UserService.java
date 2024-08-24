package ru.kata.spring.boot_security.demo.service;

import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    @Transactional
    void saveUser(User user) ;

    @Transactional
    void removeUserById(int id);

    List<User> getAllUsers();

    @Transactional
    void updateUser(User user);

    User getUserById(int id);

    Optional<User> getUserByUsername(String username);
}
