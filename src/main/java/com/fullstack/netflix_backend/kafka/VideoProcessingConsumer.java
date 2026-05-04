package com.fullstack.netflix_backend.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.fullstack.netflix_backend.dto.Video;
import com.fullstack.netflix_backend.dto.VideoUploadedEvent;
import com.fullstack.netflix_backend.repositories.VideoRepository;


@Service
public class VideoProcessingConsumer {

    @Autowired
    private VideoProcessingService processingService;

    @Autowired
    private VideoRepository videoRepository;

    @KafkaListener(topics = "video-uploaded", groupId = "video-group")
    public void process(VideoUploadedEvent event) {

        System.out.println("🎬 Received event for: " + event.getFileName());

        try {
            String thumbnailPath =
                    processingService.generateThumbnail(event.getFileName());

            // ✅ save in DB
            Video video = videoRepository
                    .findById(event.getVideoId())
                    .orElseThrow();

            video.setThumbnailPath(thumbnailPath);
            videoRepository.save(video);

            System.out.println("✅ Thumbnail saved: " + thumbnailPath);

        } catch (Exception e) {
            System.out.println("❌ Thumbnail generation failed");
            e.printStackTrace();
        }
    }
}