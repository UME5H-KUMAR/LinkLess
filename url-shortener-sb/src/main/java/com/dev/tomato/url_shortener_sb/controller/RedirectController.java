package com.dev.tomato.url_shortener_sb.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.dev.tomato.url_shortener_sb.entity.UrlMapping;
import com.dev.tomato.url_shortener_sb.service.UrlMappingService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class RedirectController {


    private final UrlMappingService urlMappingService;


    @GetMapping("/{shortUrl}")
    public ResponseEntity<Void>  redirect(@PathVariable String shortUrl ){

        UrlMapping urlMapping = urlMappingService.getOriginalUrl(shortUrl);
        
        if(urlMapping != null){
            HttpHeaders httpHeaders= new HttpHeaders();

            httpHeaders.add("Location", urlMapping.getOriginalUrl());

            return ResponseEntity.status(302).headers(httpHeaders).build();
        }
        else{
            return ResponseEntity.notFound().build();
        }
    } 
}
