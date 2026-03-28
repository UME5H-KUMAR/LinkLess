package com.dev.tomato.url_shortener_sb.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dev.tomato.url_shortener_sb.dto.LoginRequestDto;
import com.dev.tomato.url_shortener_sb.dto.LoginResponseDto;
import com.dev.tomato.url_shortener_sb.dto.SignupRequestDto;
import com.dev.tomato.url_shortener_sb.dto.SignupResponseDto;
import com.dev.tomato.url_shortener_sb.security.AuthService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;


@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<SignupResponseDto> signup(@RequestBody SignupRequestDto signupRequestDto){
        
        return ResponseEntity.ok(authService.signup(signupRequestDto));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login( @RequestBody LoginRequestDto loginRequestDto){

        return ResponseEntity.ok(authService.login(loginRequestDto));
    }
}
