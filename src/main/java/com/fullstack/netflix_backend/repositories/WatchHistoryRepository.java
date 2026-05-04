package com.fullstack.netflix_backend.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fullstack.netflix_backend.dto.WatchHistory;

public interface WatchHistoryRepository extends JpaRepository<WatchHistory, Long> {

    Optional<WatchHistory> findByUserIdAndVideoId(int userId, int videoId);

    List<WatchHistory> findByUserIdOrderByUpdatedAtDesc(int userId);
}
