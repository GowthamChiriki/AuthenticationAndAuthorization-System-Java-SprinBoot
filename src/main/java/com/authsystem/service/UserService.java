package com.authsystem.service;

import com.authsystem.dao.RoleDao;
import com.authsystem.dao.UserDao;
import com.authsystem.entity.Role;
import com.authsystem.entity.User;

import java.util.List;

public class UserService {
    private final UserDao userDao = new UserDao();
    private final RoleDao roleDao = new RoleDao();

    public UserService() {}

    public User getById(Long id) {
        return userDao.findById(id);
    }

    public List<User> getAllUsers() {
        return userDao.findAll();
    }

    public User updateProfile(Long userId, String newEmail, String newUsername) {
        User user = userDao.findById(userId);
        if (user == null) throw new IllegalArgumentException("User not found");
        if (newEmail != null && !newEmail.trim().isEmpty()) user.setEmail(newEmail);
        if (newUsername != null && !newUsername.trim().isEmpty()) user.setUsername(newUsername);
        return userDao.save(user);
    }

    public void changePassword(Long userId, String newHashedPassword) {
        User user = userDao.findById(userId);
        if (user == null) throw new IllegalArgumentException("User not found");
        user.setPasswordHash(newHashedPassword);
        userDao.save(user);
    }

    public User createUserAsAdmin(String username, String email, String passwordHash, String roleName) {
        Role role = roleDao.findByName(roleName);
        if (role == null) role = roleDao.save(new Role(roleName));
        User user = new User(username, email, passwordHash, role);
        return userDao.save(user);
    }

    public void deleteUser(Long id) {
        User u = userDao.findById(id);
        if (u == null) throw new IllegalArgumentException("User not found");
        userDao.delete(u);
    }

    public void assignRole(Long userId, String roleName) {
        User u = userDao.findById(userId);
        if (u == null) throw new IllegalArgumentException("User not found");
        Role r = roleDao.findByName(roleName);
        if (r == null) r = roleDao.save(new Role(roleName));
        u.setRole(r);
        userDao.save(u);
    }
}
