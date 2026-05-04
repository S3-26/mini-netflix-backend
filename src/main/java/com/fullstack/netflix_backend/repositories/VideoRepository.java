package com.fullstack.netflix_backend.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fullstack.netflix_backend.dto.Video;

public interface VideoRepository extends JpaRepository<Video,Integer>{
    Optional<Video> findByFileName(String fileName);
    
}