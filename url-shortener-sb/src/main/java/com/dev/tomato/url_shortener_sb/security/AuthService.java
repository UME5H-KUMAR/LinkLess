package com.dev.tomato.url_shortener_sb.security;

import java.util.Set;


import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.dev.tomato.url_shortener_sb.dto.LoginRequestDto;
import com.dev.tomato.url_shortener_sb.dto.LoginResponseDto;
import com.dev.tomato.url_shortener_sb.dto.SignupRequestDto;
import com.dev.tomato.url_shortener_sb.dto.SignupResponseDto;
import com.dev.tomato.url_shortener_sb.entity.User;
import com.dev.tomato.url_shortener_sb.entity.type.RoleType;
import com.dev.tomato.url_shortener_sb.repository.UserRepository;
import com.dev.tomato.url_shortener_sb.security.jwt.JwtUtil;
import com.dev.tomato.url_shortener_sb.service.UserDetailsImpl;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {


    private final UserRepository userRepository;

    private final AuthenticationManager authenticationManager;

    private final ModelMapper modelMapper;

    private final PasswordEncoder passwordEncoder;

    private final JwtUtil jwtUtil;

    

    public SignupResponseDto signup(SignupRequestDto signupRequestDto){
        

        if(userRepository.findByUsername(signupRequestDto.getUsername()).isPresent()) throw new IllegalArgumentException("Username Already Exist");

        User user= User.builder()
            .username(signupRequestDto.getUsername())
            .email(signupRequestDto.getEmail())
            .password(passwordEncoder.encode(signupRequestDto.getPassword()))
            .roles(Set.of(RoleType.USER))
            .build();
        
        userRepository.save(user);

        return modelMapper.map(user, SignupResponseDto.class);

    }


    public LoginResponseDto login(LoginRequestDto loginRequestDto){
        
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(loginRequestDto.getUsername(), loginRequestDto.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        String jwt= jwtUtil.generateJwtToken(userDetails);

        return  new LoginResponseDto(jwt, userDetails.getId());


    }
}
