package com.dev.tomato.url_shortener_sb.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ClickEventDto {

    private Long count;
    private LocalDateTime clickDate;
}
