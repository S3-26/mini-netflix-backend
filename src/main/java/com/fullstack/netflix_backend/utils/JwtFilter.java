package com.fullstack.netflix_backend.utils;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fullstack.netflix_backend.dto.User;
import com.fullstack.netflix_backend.repositories.UserRepository;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private UserRepository  userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {

            String token = authHeader.substring(7);

            try {
                String email = JwtUtil.extractEmail(token);
                User user = userRepository.findByEmail(email);
                String role = JwtUtil.extractRole(token);
                // Set authentication in Spring context
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                user,
                                null,
                                List.of(new SimpleGrantedAuthority(role))
                        );

                SecurityContextHolder.getContext().setAuthentication(authentication);

            } catch (Exception e) {
                //  Invalid token → do nothing (request will fail later)
            }
        }

        //  Always continue filter chain
        chain.doFilter(request, response);
    }
}