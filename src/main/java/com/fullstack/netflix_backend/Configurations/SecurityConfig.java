package com.fullstack.netflix_backend.Configurations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fullstack.netflix_backend.dto.User;
import com.fullstack.netflix_backend.repositories.UserRepository;
import com.fullstack.netflix_backend.utils.JwtFilter;
import com.fullstack.netflix_backend.utils.JwtUtil;

@Configuration
public class SecurityConfig {

    @Autowired
    UserRepository userRepository;
    @Bean
    public JwtFilter jwtFilter() {
        return new JwtFilter();
        }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
            .cors(cors -> {})
            .csrf(csrf -> csrf.disable())
            .oauth2Login(oauth -> oauth
                .successHandler((request, response, authentication) -> {

                    OAuth2AuthenticationToken auth = (OAuth2AuthenticationToken) authentication;
                    OAuth2User oauthUser = auth.getPrincipal();
                    String email = oauthUser.getAttribute("email");
                    
                    String role = authentication.getAuthorities().stream()
                    .map(Object::toString)
                    .findFirst()
                    .orElse("USER");
                    String token = JwtUtil.generateToken(email, role);
                        

                    User user = new User();
                    user.setEmail(email);
                    user.setPassword("oauth2user");
                    if(role.equals("OAUTH2_USER"))
                        role = "USER";
                    user.setRole(role);
                    if(userRepository.findByEmail(email) == null)
                    userRepository.save(user);
                    response.sendRedirect(
                        "http://localhost:5173/oauth-success?token=" + token
                    );
                })
            )
            //  ADD JWT FILTER HERE
        .addFilterBefore(jwtFilter(), UsernamePasswordAuthenticationFilter.class)
        .authorizeHttpRequests(auth -> auth
                // Allow OAuth endpoints
                .requestMatchers(
                    "/api/auth/login",
                    "/api/auth/signup",
                    "/oauth2/**",
                    "/error"
                ).permitAll()
                // 🔒 Admin only FIRST
                .requestMatchers("/api/videos/upload").hasAuthority("ADMIN")
                .requestMatchers("/api/videos/**", "/thumbnails/**").permitAll()
                .requestMatchers("/api/videos/upload").hasAuthority("ADMIN")
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                // Secure everything else
                .anyRequest().authenticated()
            );
        return http.build();
    }


}