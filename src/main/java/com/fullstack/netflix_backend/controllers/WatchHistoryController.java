package com.fullstack.netflix_backend.controllers;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fullstack.netflix_backend.dto.User;
import com.fullstack.netflix_backend.dto.Video;
import com.fullstack.netflix_backend.dto.WatchHistory;
import com.fullstack.netflix_backend.repositories.VideoRepository;
import com.fullstack.netflix_backend.repositories.WatchHistoryRepository;

@RestController
@RequestMapping("/api/watch")
@CrossOrigin(origins = "*")
public class WatchHistoryController {

    @Autowired
    private WatchHistoryRepository repo;
    
    @Autowired
    private VideoRepository  videoRepository;

    // 🔹 Save progress
    @PostMapping("/progress")
    public ResponseEntity<?> saveProgress(
            @AuthenticationPrincipal User user,
            @RequestParam String fileName,
            @RequestParam double progress,
            @RequestParam double duration) {

       Video video = videoRepository.findByFileName(fileName).orElse(null);
    if (video == null) return ResponseEntity.notFound().build();

    WatchHistory history = repo
        .findByUserIdAndVideoId(user.getId(), video.getId())
        .orElse(new WatchHistory());

    history.setUserId(user.getId());
    history.setVideoId(video.getId());
    history.setProgress(progress);
    history.setDuration(duration);
    history.setUpdatedAt(LocalDateTime.now());

    repo.save(history);

        return ResponseEntity.ok().build();
    }

    // 🔹 Get progress (resume)
   @GetMapping("/progress")
public ResponseEntity<Double> getProgress(
        @AuthenticationPrincipal User user,
        @RequestParam String fileName) {

    Video video = videoRepository.findByFileName(fileName).orElse(null);
    if (video == null) return ResponseEntity.ok(0.0);

    return repo.findByUserIdAndVideoId(user.getId(), video.getId())
            .map(h -> ResponseEntity.ok(h.getProgress()))
            .orElse(ResponseEntity.ok(0.0));
}

    // 🔹 Continue watching list
   @GetMapping("/continue")
public List<Map<String, Object>> getContinue(@AuthenticationPrincipal User user) {

    return repo.findByUserIdOrderByUpdatedAtDesc(user.getId())
        .stream()
        .map(h -> {
            Video v = videoRepository.findById(h.getVideoId()).orElse(null);

            Map<String, Object> map = new HashMap<>();
            map.put("fileName", v.getFileName());
            map.put("thumbnailPath", v.getThumbnailPath());
            map.put("title", v.getTitle());
            map.put("progress", h.getProgress());
            map.put("duration", h.getDuration());
            return map;
        })
        .toList();
}
}
