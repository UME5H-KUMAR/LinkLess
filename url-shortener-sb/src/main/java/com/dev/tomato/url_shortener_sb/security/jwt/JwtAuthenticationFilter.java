package com.dev.tomato.url_shortener_sb.security.jwt;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.dev.tomato.url_shortener_sb.service.UserDetailsServiceImpl;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter{


    private final JwtUtil jwtUtil;

    private final UserDetailsServiceImpl userDetailsServiceImpl;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

            try{

                final String requestTokenHeader= request.getHeader("Authorization");
                
                if(requestTokenHeader == null || !requestTokenHeader.startsWith("Bearer")){
                    filterChain.doFilter(request, response);
                    return;
                }

                String jwt= jwtUtil.getJwtFromHeader(request);

                
                
                if(jwt != null && jwtUtil.validateToken(jwt)){
                    String username= jwtUtil.getUsernameFromJwtToken(jwt);
                    UserDetails userDetails= userDetailsServiceImpl.loadUserByUsername(username);

                    if(userDetails != null){
                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken( userDetails, null , userDetails.getAuthorities());

                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                }
            }
            catch(Exception ex){
                ex.printStackTrace();
            }
            filterChain.doFilter(request, response);
    }


}
