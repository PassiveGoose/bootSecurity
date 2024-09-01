package ru.kata.spring.boot_security.demo.service;

import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    void saveUser(User user) ;

    void removeUserById(int id);

    List<User> getAllUsers();

    void updateUser(User user);

    User getUserById(int id);

    Optional<User> getUserByUsername(String username);

    Optional<Role> getRoleByName(String roleName);
}
