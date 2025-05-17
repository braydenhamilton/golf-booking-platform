package com.golf.teetimecoreapi.security;

import com.golf.teetimecoreapi.model.UserEntity;
import com.golf.teetimecoreapi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String golfNZMemberId) throws UsernameNotFoundException {
        UserEntity user = userRepository.findByGolfNZMemberId(golfNZMemberId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with golfNZMemberId: " + golfNZMemberId));

        return new User(
                user.getGolfNZMemberId(),
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }
} 