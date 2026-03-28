package com.dev.tomato.url_shortener_sb.dto;

import lombok.Data;

@Data
public class SignupRequestDto {

    private String username;
    private String email;
    private String password;

}
