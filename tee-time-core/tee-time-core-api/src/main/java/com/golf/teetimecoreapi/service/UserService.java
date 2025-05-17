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
        UserEntity user = new UserEntity();
        user.setGolfNZMemberId(config.getGolfNZMemberId());
        user.setUsername(config.getUsername());
        user.setPassword(passwordEncoder.encode(config.getPassword()));
        user.setEmail(config.getEmail());
        user.setGolfNZPassword(config.getGolfNZPassword());

        UserEntity savedUser = userRepository.save(user);
        return new UserResponse()
                .golfNZMemberId(savedUser.getGolfNZMemberId())
                .username(savedUser.getUsername());
    }

    public UserResponse loginUser(User user) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getGolfNZMemberId(), user.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String jwt = jwtTokenUtil.generateToken(userDetails);

        return new UserResponse()
                .golfNZMemberId(user.getGolfNZMemberId())
                .username(user.getUsername())
                .token(jwt);
    }

    public void logoutUser() {
        SecurityContextHolder.clearContext();
    }
}
