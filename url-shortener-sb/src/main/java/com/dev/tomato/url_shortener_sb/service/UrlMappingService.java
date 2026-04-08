package com.dev.tomato.url_shortener_sb.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dev.tomato.url_shortener_sb.dto.ClickEventDto;
import com.dev.tomato.url_shortener_sb.dto.UrlMappingDto;
import com.dev.tomato.url_shortener_sb.entity.ClickEvent;
import com.dev.tomato.url_shortener_sb.entity.UrlMapping;
import com.dev.tomato.url_shortener_sb.entity.User;
import com.dev.tomato.url_shortener_sb.repository.ClickEventRepository;
import com.dev.tomato.url_shortener_sb.repository.UrlMappingRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UrlMappingService {

    private final UrlMappingRepository urlMappingRepository;

    private final ModelMapper modelMapper;

    private final ClickEventRepository clickEventRepository;

    public UrlMappingDto createShortUrl(String originalUrl, User user) {

        String shortUrl= generateShortUrl(originalUrl);

        UrlMapping urlMapping = UrlMapping.builder()
            .originalUrl(originalUrl)
            .shortUrl(shortUrl)
            .user(user)
            .createdAt(LocalDateTime.now())
            .build();
        
        urlMappingRepository.save(urlMapping);

        
        UrlMappingDto urlMappingDto = modelMapper.map(urlMapping, UrlMappingDto.class);
        urlMappingDto.setUsername(user.getUsername());

        return urlMappingDto;
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


    public List<UrlMappingDto> getUrlsByUser(User user){

        return urlMappingRepository.findByUser(user).stream()
                .map(urlMapping -> {
                    UrlMappingDto urlMappingDto = modelMapper.map(urlMapping, UrlMappingDto.class);
                    urlMappingDto.setUsername(urlMapping.getUser().getUsername());
                    return urlMappingDto;
                })
                .collect(Collectors.toList());
    }

    public List<ClickEventDto> getClickEventsByDate(String shortUrl, LocalDateTime start, LocalDateTime end){

        UrlMapping urlMapping = urlMappingRepository.findByShortUrl(shortUrl);

        if(urlMapping != null){
            return clickEventRepository.findByUrlMappingAndClickDateBetween(urlMapping, start, end).stream()
                .map(event -> modelMapper.map(event, ClickEventDto.class))
                .collect(Collectors.toList());
        }
        return null;
    }

    public Map<LocalDate, Long> getTotalClicksByUserAndDate(User user, LocalDateTime start, LocalDateTime end){

        List<UrlMapping> urlMappings= urlMappingRepository.findByUser(user);

        List<ClickEvent> clickEvents= clickEventRepository.findByUrlMappingInAndClickDateBetween(urlMappings, start, end);

        return clickEvents.stream()
                .collect(Collectors.groupingBy(click -> click.getClickDate().toLocalDate(), Collectors.counting()));

    }

    @Transactional
    public UrlMapping getOriginalUrl(String shortUrl){
        UrlMapping urlMapping= urlMappingRepository.findByShortUrl(shortUrl);

        if (urlMapping == null) {
            return null;
        }

        urlMapping.setClickCount(urlMapping.getClickCount() + 1);
        urlMappingRepository.save(urlMapping);

        ClickEvent clickEvent = new ClickEvent();
        clickEvent.setClickDate(LocalDateTime.now());
        clickEvent.setUrlMapping(urlMapping);
        clickEventRepository.save(clickEvent);

        return urlMapping;
    }
}
