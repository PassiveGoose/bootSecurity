package ru.kata.spring.boot_security.demo.service;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.dao.UserDao;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserDao userDao;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserDao userDao, PasswordEncoder passwordEncoder) {
        this.userDao = userDao;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public boolean saveUser(@Valid User user, List<String> roles) {
        setRolesToUser(user, roles);
        if (checkUserIsAlreadyInDb(user)) {
            return true;
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userDao.saveUser(user);
        return false;
    }

    @Override
    public void removeUserById(int id) {
        userDao.removeUserById(id);
    }

    @Override
    public List<User> getAllUsers() {
        return userDao.getAllUsers();
    }

    @Override
    public boolean updateUser(@Valid User user, List<String> roles) {
        setRolesToUser(user, roles);
        if (checkUserIsAlreadyInDb(user)) {
            return false;
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userDao.updateUser(user);
        return true;
    }

    @Override
    public User getUserById(int id) {
        return userDao.getUserById(id);
    }

    @Override
    public Optional<User> getUserByUsername(String username) {
        List<User> userList = getAllUsers();
        return userList.stream().filter(user -> user.getUsername().equals(username)).findAny();
    }

    @Override
    public Optional<Role> getRoleByName(String roleName) {
        return userDao.getRoleByName(roleName);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> foundedUser = getUserByUsername(username);
        if (foundedUser.isEmpty()) {
            throw new UsernameNotFoundException(username + " not found");
        }
        return foundedUser.get();
    }

    private void setRolesToUser(User user, List<String> roles) {
        List<Role> rolesForUser = new ArrayList<>();
        for (String role : roles) {
            Optional<Role> roleOpt = getRoleByName(role);
            Role roleForUser = roleOpt.orElseGet(() -> new Role(role));
            rolesForUser.add(roleForUser);
        }
        user.setRoles(rolesForUser);
    }

    private boolean checkUserIsAlreadyInDb(User user) {
        Optional<User> userInDB = getUserByUsername(user.getUsername());
        return userInDB.isPresent() && userInDB.get().getId() != user.getId();
    }
}