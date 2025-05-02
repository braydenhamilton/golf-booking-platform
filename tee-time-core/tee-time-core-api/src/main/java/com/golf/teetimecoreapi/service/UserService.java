package com.golf.teetimecoreapi.service;

import com.golf.model.NewUserConfiguration;
import com.golf.model.User;
import com.golf.model.UserResponse;
import com.golf.teetimecoreapi.model.UserEntity;
import com.golf.teetimecoreapi.repository.UserRepository;
import com.golf.teetimecoreapi.session.UserSessionStore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public UserResponse registerNewUser(NewUserConfiguration config) {
        UserEntity user = new UserEntity();
        user.setGolfNZMemberId(config.getGolfNZMemberId());
        user.setUsername(config.getUsername());
        user.setPassword(config.getPassword()); // Hash before storing in production
        user.setEmail(config.getEmail());
        user.setGolfNZPassword(config.getGolfNZPassword());


        UserEntity savedUser = userRepository.save(user);
        return new UserResponse().golfNZMemberId(savedUser.getGolfNZMemberId()).username(savedUser.getUsername());
    }

    public UserResponse loginUser(User user) {
        Optional<UserEntity> found = userRepository.findByGolfNZMemberId(user.getGolfNZMemberId());
        if (found.isPresent() && found.get().getPassword().equals(user.getPassword())) {
            // Add to session store
            UserSessionStore.addUserSession(user);

            return new UserResponse().golfNZMemberId(user.getGolfNZMemberId()).username(user.getUsername());
        }
        throw new RuntimeException("Invalid credentials");
    }

    public void logoutUser() {
        // For simplicity, remove all sessions or use user-specific logout logic
    }
}
