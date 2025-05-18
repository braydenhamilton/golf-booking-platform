package com.golf.teetimecoreapi.resource;

import com.golf.api.UserApi;
import com.golf.model.NewUserConfiguration;
import com.golf.model.User;
import com.golf.model.UserResponse;
import com.golf.teetimecoreapi.service.UserService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class UserResource implements UserApi {

    private static final Log LOGGER = LogFactory.getLog(UserResource.class);

    private final UserService userService;

    @Autowired
    public UserResource(UserService userService) {
        this.userService = userService;
    }


    @Override
    public ResponseEntity<UserResponse> registerUser(@Valid @RequestBody NewUserConfiguration newUserConfiguration) {
        LOGGER.info("Received user registration request: " + newUserConfiguration);

        try {
            // Execute user registration logic
            UserResponse userResponse = userService.registerNewUser(newUserConfiguration);
            return ResponseEntity.status(201).body(userResponse);
        } catch (RuntimeException e) {
            LOGGER.error("User registration failed: " + e.getMessage());
            if (e.getMessage().contains("already exists") || e.getMessage().contains("already taken")) {
                return ResponseEntity.status(409).build(); // 409 Conflict
            }
            return ResponseEntity.status(400).build(); // 400 Bad Request
        }
    }

    @Override
    public ResponseEntity<UserResponse> loginUser(@Valid @RequestBody User user) {
        LOGGER.info("Received user login request: " + user);

        // Execute user login logic
        UserResponse userResponse = userService.loginUser(user);

        return ResponseEntity.ok(userResponse);
    }

    @Override
    public ResponseEntity<Void> logoutUser() {
        LOGGER.info("Received user logout request");

        // Execute user logout logic
        userService.logoutUser();

        return ResponseEntity.ok().build();
    }




}
