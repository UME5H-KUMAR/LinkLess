package com.dev.tomato.url_shortener_sb.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dev.tomato.url_shortener_sb.dto.ClickEventDto;
import com.dev.tomato.url_shortener_sb.dto.UrlMappingDto;
import com.dev.tomato.url_shortener_sb.entity.User;
import com.dev.tomato.url_shortener_sb.service.UrlMappingService;
import com.dev.tomato.url_shortener_sb.service.UserService;

import lombok.RequiredArgsConstructor;

import java.security.Principal;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;



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
    
    
    @GetMapping("/myUrls")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<UrlMappingDto>> getUserUrls(Principal principal){

        User user = userService.findByUsername(principal.getName());

        List<UrlMappingDto> urls= urlMappingService.getUrlsByUser(user);

        return ResponseEntity.ok(urls);
    }


    @GetMapping("/analytics/{shortUrl}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<ClickEventDto>> getUrlAnalytics(@PathVariable String shortUrl,
                                                            @RequestParam("startDate") String startDate,
                                                            @RequestParam("endDate") String endDate){
                                                  
                                                                
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

        LocalDateTime start= LocalDateTime.parse(startDate, formatter);
        LocalDateTime end= LocalDateTime.parse(endDate, formatter);
        
        List<ClickEventDto> clickEvents= urlMappingService.getClickEventsByDate(shortUrl, start, end);

        return ResponseEntity.ok(clickEvents);

    }


    @GetMapping("/totalClicks")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Map<LocalDate, Long>> getTotalClicksByDate(Principal principal,
                                                                    @RequestParam("startDate") String startDate,
                                                                    @RequestParam("endDate") String endDate
    ){
        User user = userService.findByUsername(principal.getName());

        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

        LocalDateTime start= LocalDateTime.parse(startDate, formatter);
        LocalDateTime end= LocalDateTime.parse(endDate, formatter);
        
        Map<LocalDate, Long> totalClicks= urlMappingService.getTotalClicksByUserAndDate(user, start, end);


        return ResponseEntity.ok(totalClicks);

        
    }
    


}
