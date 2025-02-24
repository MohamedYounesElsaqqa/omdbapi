package com.omdb.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.omdb.model.Movie;
import com.omdb.model.Rating;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MovieDTO {
    @JsonProperty("imdbID")
    private String imdbID;

    @JsonProperty("Title")
    private String title;

    @JsonProperty("Year")
    private String year;

    @JsonProperty("Poster")
    private String poster;

    @JsonProperty("Type")
    private String type;
    private double averageRating;

    public Movie convertToEntity() {
        Movie movie = new Movie();
        movie.setImdbID(this.imdbID);
        movie.setTitle(this.title);
        movie.setYear(this.year);
        movie.setPoster(this.poster);
        movie.setType(this.type);
        return movie;
    }
    public MovieDTO(Movie movie) {
        this.imdbID = movie.getImdbID();
        this.title = movie.getTitle();
        this.year = movie.getYear();
        this.poster = movie.getPoster();
        this.type = movie.getType();
        if (movie.getRatings() != null && !movie.getRatings().isEmpty()) {
            this.averageRating = movie.getRatings().stream()
                    .mapToInt(Rating::getScore)
                    .average()
                    .orElse(0.0);
            this.averageRating = Math.round(averageRating * 10.0) / 10.0;
        } else {
            this.averageRating = 0.0;
        }
    }

}
