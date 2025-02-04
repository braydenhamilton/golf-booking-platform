package com.golf.teetimecoreapi.service;

import com.golf.model.NewUserConfiguration;
import com.golf.model.User;
import com.golf.model.UserResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserService {

    public UserResponse registerNewUser(NewUserConfiguration  newUserConfiguration) {
        log.info("Registering user: " + newUserConfiguration);
        // Register user logic
        return new UserResponse();
    }

    public UserResponse loginUser(User user) {
        log.info("Logging in user: " + user);
        // Login user logic
        return new UserResponse();
    }

    public void logoutUser() {
        log.info("Logging out user");
        // Logout user logic
    }

}
