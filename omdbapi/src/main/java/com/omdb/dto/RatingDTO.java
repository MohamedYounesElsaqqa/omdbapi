package com.omdb.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RatingDTO {
    private Long userId;
    private String imdbID;
    private int score;

    public RatingDTO(Long userId, String imdbID, int score) {
        this.userId = userId;
        this.imdbID = imdbID;
        this.score = score;
    }

}
