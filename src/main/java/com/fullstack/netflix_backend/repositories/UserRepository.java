package com.fullstack.netflix_backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.fullstack.netflix_backend.dto.User;

public interface UserRepository extends JpaRepository<User, Integer> {
    User findByEmail(String email);
} 