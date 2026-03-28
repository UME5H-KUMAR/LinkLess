package com.dev.tomato.url_shortener_sb.service;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.dev.tomato.url_shortener_sb.entity.User;
import com.dev.tomato.url_shortener_sb.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor

public class UserService {

    private final UserRepository userRepository;

    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(
            ()-> new UsernameNotFoundException("User not found with username: "+ username)
        );
    }
}
