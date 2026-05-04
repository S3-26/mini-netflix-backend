package com.fullstack.netflix_backend.controllers;
import com.fullstack.netflix_backend.dto.User;
import com.fullstack.netflix_backend.dto.Video;
import com.fullstack.netflix_backend.dto.Watchlist;
import com.fullstack.netflix_backend.repositories.VideoRepository;
import com.fullstack.netflix_backend.repositories.WatchlistRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/watchlist")
public class WatchlistController{

    @Autowired
    WatchlistRepository watchlistRepository;

    @Autowired
    VideoRepository videoRepository;

    @PostMapping(path = "/toggle")
    public void  toggleWatchlist(@RequestParam int videoId, @AuthenticationPrincipal User user)
    {
        Video video = videoRepository.findById(videoId).orElseThrow(() -> new RuntimeException("Video not found"));
       if(!watchlistRepository.existsByUserAndVideo(user, video)) 
        {
        Watchlist item = new Watchlist();
        item.setUser(user);
        item.setVideo(video);
        watchlistRepository.save(item);
          }
         else{
        watchlistRepository.deleteByUserAndVideo(user, video);
       }
    }

    @GetMapping
    public List<Video> getWatchlistByUserId(@AuthenticationPrincipal User user)
    {
        return watchlistRepository.findByUserId(user.getId()).stream().map(Watchlist::getVideo).toList();
    }

}