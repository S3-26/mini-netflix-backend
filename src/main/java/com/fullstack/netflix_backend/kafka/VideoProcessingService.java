package com.fullstack.netflix_backend.kafka;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

@Service
public class VideoProcessingService {

    private static final String VIDEO_DIR = "videos/";
    private static final String THUMB_DIR = "thumbnails/";

    @Value("${ffmpeg.path:C:\\\\ffmpeg\\\\ffmpeg-8.1-essentials_build\\\\bin\\\\ffmpeg.exe}")
    private String ffmpegPath;

    public String generateThumbnail(String fileName) throws Exception {

        String inputPath = VIDEO_DIR + fileName;
        String outputPath = THUMB_DIR + fileName + ".png";

        // create folder if not exists
        File dir = new File(THUMB_DIR);
        if (!dir.exists()) dir.mkdirs();

        ProcessBuilder pb = new ProcessBuilder(
                ffmpegPath,
                "-ss", "00:00:02",
                "-i", inputPath,
                "-vframes", "1",
                outputPath
        );

        pb.redirectErrorStream(true);

        Process process = pb.start();

        // 🔥 read logs (IMPORTANT for debugging)
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream())
        );

        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }

        int exitCode = process.waitFor();

        if (exitCode != 0) {
            throw new RuntimeException("FFmpeg failed");
        }

        return outputPath;
    }
}