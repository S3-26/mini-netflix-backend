package com.fullstack.netflix_backend.dto;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Video{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    String title;
    String fileName;
    String filePath;
    private String thumbnailPath;

    @JsonIgnore
    @OneToMany(mappedBy = "video", cascade = CascadeType.ALL)
    private List<Watchlist> watchlists;
}
