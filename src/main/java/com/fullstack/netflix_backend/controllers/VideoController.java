package com.fullstack.netflix_backend.controllers;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpRange;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;

import com.fullstack.netflix_backend.dto.Video;
import com.fullstack.netflix_backend.dto.VideoUploadedEvent;
import com.fullstack.netflix_backend.kafka.KafkaProducerService;
import com.fullstack.netflix_backend.repositories.VideoRepository;

@RestController
@RequestMapping("/api/videos")
public class VideoController {

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    KafkaProducerService kafkaProducerService;

    private final String VIDEO_DIR = "videos";

    // ✅ Upload Video
    @PostMapping("/upload")
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file) {
        try {
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path path = Paths.get(VIDEO_DIR).resolve(fileName);

            Files.createDirectories(path.getParent());
            Files.write(path, file.getBytes());

            Video video = new Video();
            video.setTitle(file.getOriginalFilename());
            video.setFileName(fileName);
            video.setFilePath(path.toAbsolutePath().toString());
            System.out.println("UPLOAD HIT");
            videoRepository.save(video);

            VideoUploadedEvent event = new VideoUploadedEvent();
            event.setFileName(fileName);    
            event.setVideoId(video.getId());
            kafkaProducerService.sendVideoUploadedEvent(event);

            return ResponseEntity.ok("Uploaded successfully");

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Upload failed");
        }
    }

    // ✅ Stream Video (Supports HTTP Range Requests)
    @GetMapping("/{fileName}")
public ResponseEntity<ResourceRegion> streamVideo(
        @PathVariable String fileName,
        @RequestHeader org.springframework.http.HttpHeaders headers) throws Exception {

    FileSystemResource video = new FileSystemResource("videos/" + fileName);

    if (!video.exists()) {
        return ResponseEntity.notFound().build();
    }

    long contentLength = video.contentLength();

    ResourceRegion region;

    final long chunkSize = 1024 * 1024; // 1MB

    if (headers.getRange().isEmpty()) {
        region = new ResourceRegion(video, 0, chunkSize);
    } else {
        HttpRange range = headers.getRange().get(0);
        long start = range.getRangeStart(contentLength);
        long end = range.getRangeEnd(contentLength);

        long rangeLength = Math.min(chunkSize, end - start + 1);
        region = new ResourceRegion(video, start, rangeLength);
    }

    return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
            .contentType(MediaTypeFactory.getMediaType(video)
                    .orElse(MediaType.APPLICATION_OCTET_STREAM))
            .body(region);
}

    @GetMapping(path  = "/meta/{fileName}")
    public ResponseEntity<Video> getVideoMetadata(@PathVariable String fileName) {
        return videoRepository.findByFileName(fileName)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
    }

    // ✅ Get All Videos (Metadata)
    @GetMapping(path  = "/all")
    public List<Video> getVideos() {
        return videoRepository.findAll();
    }
}