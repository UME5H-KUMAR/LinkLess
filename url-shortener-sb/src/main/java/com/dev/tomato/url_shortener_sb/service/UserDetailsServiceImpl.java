package com.dev.tomato.url_shortener_sb.service;



import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.dev.tomato.url_shortener_sb.entity.User;
import com.dev.tomato.url_shortener_sb.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
         
        User user = userRepository.findByUsername(username).orElseThrow(()-> new UsernameNotFoundException("User not found with username: "+username));

        return UserDetailsImpl.build(user);

    }

}
