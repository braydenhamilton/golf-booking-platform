package com.golf.teetimecoreapi.service;

import com.golf.model.NewUserConfiguration;
import com.golf.model.User;
import com.golf.model.UserResponse;
import com.golf.teetimecoreapi.model.UserEntity;
import com.golf.teetimecoreapi.repository.UserRepository;
import com.golf.teetimecoreapi.security.JwtTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    public UserResponse registerNewUser(NewUserConfiguration config) {
        // Check if user already exists with this GolfNZ member ID
        if (userRepository.findByGolfNZMemberId(config.getGolfNZMemberId()).isPresent()) {
            throw new RuntimeException("User with GolfNZ member ID " + config.getGolfNZMemberId() + " already exists");
        }

        // Check if username is already taken
        if (userRepository.findByUsername(config.getUsername()).isPresent()) {
            throw new RuntimeException("Username " + config.getUsername() + " is already taken");
        }

        UserEntity user = new UserEntity();
        user.setGolfNZMemberId(config.getGolfNZMemberId());
        user.setUsername(config.getUsername());
        user.setPassword(passwordEncoder.encode(config.getPassword()));
        user.setEmail(config.getEmail());
        user.setGolfNZPassword(passwordEncoder.encode(config.getGolfNZPassword()));

        UserEntity savedUser = userRepository.save(user);
        return new UserResponse()
                .golfNZMemberId(savedUser.getGolfNZMemberId())
                .username(savedUser.getUsername());
    }

    public UserResponse loginUser(User user) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String jwt = jwtTokenUtil.generateToken(userDetails);

        // Get the actual user entity to return complete user info
        UserEntity userEntity = userRepository.findByUsername(user.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found after authentication"));

        return new UserResponse()
                .golfNZMemberId(userEntity.getGolfNZMemberId())
                .username(userEntity.getUsername())
                .email(userEntity.getEmail())
                .token(jwt);
    }

    public void logoutUser() {
        SecurityContextHolder.clearContext();
    }
}
