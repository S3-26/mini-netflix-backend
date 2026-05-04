package com.fullstack.netflix_backend.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import com.fullstack.netflix_backend.dto.User;
import com.fullstack.netflix_backend.repositories.UserRepository;
import com.fullstack.netflix_backend.utils.JwtUtil;

@RestController
@RequestMapping("/api/auth")
public class AuthController 
{
    @Autowired 
    UserRepository userRepository;

    @PostMapping(path = "/signup")
    public void signup(@RequestBody User user)
    {
        String role = user.getEmail().equals("srushti") ? "ADMIN" : "USER";
        user.setRole(role);
        userRepository.save(user);
    }

    @PostMapping(path = "/login")
    public @ResponseBody String login(@RequestBody User user)
    {
        User user1 = userRepository.findByEmail(user.getEmail());
        if(user1 != null && user1.getPassword().equals(user.getPassword()))
        {

            return JwtUtil.generateToken(user1.getEmail(), user1.getRole());
        }
            
        return "Invalid email or password";
    }

    @GetMapping(path="/users")
   public List<User> getAllUsers(@AuthenticationPrincipal User user)
   {
    if(user!=null)
    {
        String role=user.getRole();
       return role.equals("ADMIN") ? userRepository.findAll() : null;
    }
        return null;
   }
}
