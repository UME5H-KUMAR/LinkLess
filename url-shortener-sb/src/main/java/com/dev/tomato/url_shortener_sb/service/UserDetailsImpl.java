package com.dev.tomato.url_shortener_sb.service;

import java.util.Collection;
import java.util.Collections;

import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.dev.tomato.url_shortener_sb.entity.User;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Service
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserDetailsImpl implements UserDetails{

    private Long id;

    private String username;

    private String email;

    private String password;

    private Collection<? extends GrantedAuthority> authorities;

    // build UserDetailsImpl from User entity
    public static UserDetailsImpl build(User user){

        GrantedAuthority authority= new SimpleGrantedAuthority(user.getRoles().toString());

        return new UserDetailsImpl(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getPassword(),
            Collections.singletonList(authority)
        );

        
    }



    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;

    }

    @Override
    public @Nullable String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
        
    }


}
