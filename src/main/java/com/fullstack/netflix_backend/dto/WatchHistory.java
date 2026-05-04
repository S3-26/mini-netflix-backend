package com.fullstack.netflix_backend.dto;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class WatchHistory {

    @Id
    @GeneratedValue
    private Long id;

    private int userId;
    private int videoId;

    private double progress;
    private double duration;

    private LocalDateTime updatedAt = LocalDateTime.now();
}