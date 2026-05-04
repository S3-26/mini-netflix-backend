package com.fullstack.netflix_backend.dto;

import lombok.Data;

@Data
 public class VideoUploadedEvent 
    {
        private String fileName;
        private int videoId;
    }
