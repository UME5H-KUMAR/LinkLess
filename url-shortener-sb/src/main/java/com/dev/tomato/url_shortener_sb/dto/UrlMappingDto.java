package com.dev.tomato.url_shortener_sb.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class UrlMappingDto {

    private Long id;
    private String username;
    private int clickCount;

    private LocalDateTime createdAt;
    private String originalUrl;
    private String shortUrl;
}
