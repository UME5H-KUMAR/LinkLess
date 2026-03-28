package com.dev.tomato.url_shortener_sb.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dev.tomato.url_shortener_sb.dto.UrlMappingDto;
import com.dev.tomato.url_shortener_sb.entity.User;
import com.dev.tomato.url_shortener_sb.service.UrlMappingService;
import com.dev.tomato.url_shortener_sb.service.UserService;

import lombok.RequiredArgsConstructor;

import java.security.Principal;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequiredArgsConstructor
@RequestMapping("/urls")
@EnableMethodSecurity
public class UrlMappingController {

    private final UrlMappingService urlMappingService;

    private final UserService userService;

    @PostMapping("/shorten")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<UrlMappingDto> createShortUrl(@RequestBody Map<String, String> request, 
                                                        Principal principal){
        String originalUrl= request.get("originalUrl");

        User user = userService.findByUsername(principal.getName());

        UrlMappingDto urlMappingDto = urlMappingService.createShortUrl(originalUrl, user);

        return ResponseEntity.ok(urlMappingDto);
        
        
    
    }                                                
    

}
