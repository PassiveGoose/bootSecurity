package ru.kata.spring.boot_security.demo.service;

import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    boolean saveUser(User user, List<String> roleNames) ;

    void removeUserById(int id);

    List<User> getAllUsers();

    boolean updateUser(User user, List<String> roleNames);

    User getUserById(int id);

    Optional<User> getUserByUsername(String username);

    Optional<Role> getRoleByName(String roleName);
}
