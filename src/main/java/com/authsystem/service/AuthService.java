package com.authsystem.service;

import com.authsystem.dao.RoleDao;
import com.authsystem.dao.UserDao;
import com.authsystem.entity.Role;
import com.authsystem.entity.User;
import com.authsystem.util.BCryptUtil;
import com.authsystem.util.JwtUtil;

public class AuthService {
    private final UserDao userDao = new UserDao();
    private final RoleDao roleDao = new RoleDao();

    public AuthService() {}

    public User register(String username, String email, String password) {
        if (userDao.findByUsername(username) != null) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (userDao.findByEmail(email) != null) {
            throw new IllegalArgumentException("Email already exists");
        }
        Role defaultRole = roleDao.findByName("USER");
        if (defaultRole == null) {
            defaultRole = roleDao.save(new Role("USER"));
        }
        String hash = BCryptUtil.hashPassword(password);
        User user = new User(username, email, hash, defaultRole);
        return userDao.save(user);
    }

    public String login(String usernameOrEmail, String password) {
        User user = userDao.findByUsername(usernameOrEmail);
        if (user == null) user = userDao.findByEmail(usernameOrEmail);

        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }

        if (!BCryptUtil.checkPassword(password, user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid credentials");
        }

        return JwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole().getName());
    }
}
