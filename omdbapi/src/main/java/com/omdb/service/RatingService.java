package com.omdb.service;

import com.omdb.dto.RatingDTO;
import com.omdb.exception.ResourceNotFoundException;
import com.omdb.model.AppUser;
import com.omdb.model.Movie;
import com.omdb.model.Rating;
import com.omdb.repository.AppUserRepository;
import com.omdb.repository.MovieRepository;
import com.omdb.repository.RatingRepository;
import com.omdb.response.SuccessResponse;
import org.springframework.stereotype.Service;

@Service
public class RatingService {

    private final RatingRepository ratingRepository;
    private final AppUserRepository userRepository;
    private final MovieRepository movieRepository;

    public RatingService(RatingRepository ratingRepository, AppUserRepository userRepository, MovieRepository movieRepository) {
        this.ratingRepository = ratingRepository;
        this.userRepository = userRepository;
        this.movieRepository = movieRepository;
    }

    public SuccessResponse addRating(RatingDTO ratingDTO) {
        AppUser user = userRepository.findById(ratingDTO.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Movie movie = movieRepository.findByImdbID(ratingDTO.getImdbID())
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found"));

        if (ratingDTO.getScore() < 1 || ratingDTO.getScore() > 5) {
            throw new IllegalArgumentException("Score must be between 1 and 5");
        }

        Rating rating = new Rating();
        rating.setUser(user);
        rating.setMovie(movie);
        rating.setScore(ratingDTO.getScore());
        ratingRepository.save(rating);
        return new SuccessResponse<>("Success",rating.getMovie().getImdbID());
    }
}
