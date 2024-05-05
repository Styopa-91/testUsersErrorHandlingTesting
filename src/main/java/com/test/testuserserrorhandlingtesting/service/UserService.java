package com.abi.agro_back.service;

import com.abi.agro_back.collection.User;

import java.util.List;

public interface UserService {
    User createUser(User user);

    User getUserById(String userId);

    List<User> getAllUsers();

    User updateUser(String userId, User updatedUser);

    void deleteUser(String  userId);

    User findUserByEmail(String userEmail);

    void createPasswordResetTokenForUser(User user, String token);

    String validatePasswordResetToken(String token);

    User getUserByPasswordResetToken(String token);

    void changeUserPassword(User user, String password);
}
