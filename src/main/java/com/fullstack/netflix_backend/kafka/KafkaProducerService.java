package com.fullstack.netflix_backend.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import com.fullstack.netflix_backend.dto.VideoUploadedEvent;

@Service

public class KafkaProducerService {

    @Autowired
    private  KafkaTemplate<String, VideoUploadedEvent> kafkaTemplate;

    public void sendVideoUploadedEvent(VideoUploadedEvent event) {
        kafkaTemplate.send("video-uploaded", event);
    }
}