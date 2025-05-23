package com.demo.authService.services;

import com.demo.authService.exceptions.UserAlreadyExistsException;

public interface AuthService {

    boolean signup(String email, String password) throws UserAlreadyExistsException;

    String login(String email, String password);

    boolean validate(String token);
}
