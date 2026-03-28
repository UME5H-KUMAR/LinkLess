package com.dev.tomato.url_shortener_sb.service;

import java.time.LocalDateTime;
import java.util.Random;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.dev.tomato.url_shortener_sb.dto.UrlMappingDto;
import com.dev.tomato.url_shortener_sb.entity.UrlMapping;
import com.dev.tomato.url_shortener_sb.entity.User;
import com.dev.tomato.url_shortener_sb.repository.UrlMappingRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UrlMappingService {

    private final UrlMappingRepository urlMappingRepository;

    private final ModelMapper modelMapper;

    public UrlMappingDto createShortUrl(String originalUrl, User user) {

        String shortUrl= generateShortUrl(originalUrl);

        UrlMapping urlMapping = UrlMapping.builder()
            .originalUrl(originalUrl)
            .shortUrl(shortUrl)
            .user(user)
            .createdAt(LocalDateTime.now())
            .build();
        
        urlMappingRepository.save(urlMapping);

        return modelMapper.map(urlMapping, UrlMappingDto.class);
    }

    private String generateShortUrl(String originalUrl) {
        
        Random random= new Random();
        StringBuilder shortUrl= new StringBuilder(7);

        String chars= "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

        for(int i=0; i<7; i++){
            shortUrl.append(chars.charAt(random.nextInt(chars.length())));
        }

        return shortUrl.toString();
    }
}
