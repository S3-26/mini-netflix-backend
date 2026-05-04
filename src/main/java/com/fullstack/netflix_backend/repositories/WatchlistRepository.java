package com.fullstack.netflix_backend.repositories;
import com.fullstack.netflix_backend.dto.Video;
import com.fullstack.netflix_backend.dto.Watchlist;

import jakarta.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import com.fullstack.netflix_backend.dto.User;

public interface WatchlistRepository extends JpaRepository<Watchlist, Integer> {
    
    List<Watchlist> findByUserId(int userId);
    boolean existsByUserAndVideo(User user, Video video);
    @Transactional
    void deleteByUserAndVideo(User user, Video video);
}