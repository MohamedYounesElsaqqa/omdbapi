package com.omdb.service;

import com.omdb.dto.MovieDTO;
import com.omdb.dto.MovieSearchResponseDTO;
import com.omdb.dto.OMDbResponseDTO;
import com.omdb.exception.DaplicateRecoredException;
import com.omdb.exception.ResourceNotFoundException;
import com.omdb.model.Movie;
import com.omdb.repository.MovieRepository;
import com.omdb.response.SuccessResponse;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MovieService {

    @Value("${omdb.apiKey}")
    private String apiKey;

    @Value("${omdb.url}")
    private String omdbUrl;
    @Value("${omdb.imdb}")
    private String imdbUrl;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private MovieRepository movieRepository;

    public SuccessResponse<List<MovieDTO>> searchMovies(String query, int page) {
        try {
            String url = String.format(omdbUrl, query, apiKey, page);
            ResponseEntity<MovieSearchResponseDTO> response = restTemplate.getForEntity(url, MovieSearchResponseDTO.class);

            List<MovieDTO> movies = Optional.ofNullable(response.getBody())
                    .map(MovieSearchResponseDTO::getSearch)
                    .filter(m -> !m.isEmpty())
                    .orElseThrow(() -> new Exception("No movies found for query: " + query));

            return new SuccessResponse<>("Movies retrieved successfully", movies);

        } catch (Exception ex) {
            throw new ResourceNotFoundException("Error fetching movies: " +ex.getMessage() );
        }
    }

    public SuccessResponse saveMoviesInBatches(List<MovieDTO> movies) {
        int batchSize = 10;

        try {
            /** Convert MovieDTO to Movie */
            List<Movie> movieEntities = movies.stream()
                    .map(MovieDTO::convertToEntity)
                    .collect(Collectors.toList());

            /** Check for movies with the same IMDB ID using Stream */
            movieEntities.stream()
                    .filter(movie -> movieRepository.findByImdbID(movie.getImdbID()).isPresent())
                    .findFirst()
                    .ifPresent(movie -> {
                        throw new DaplicateRecoredException("Movie with IMDB ID " + movie.getImdbID() + " already exists.");
                    });

            /** Split movies into batches and save them to the database*/
            ListUtils.partition(movieEntities, batchSize).forEach(movieRepository::saveAll);
        } catch (Exception e) {
            throw new ResourceNotFoundException("Error saving movies in batches: " + e.getMessage());
        }

        return new SuccessResponse<>("Movies saved successfully", movies.size());
    }
    public Page<MovieDTO> getAllMovies(Pageable pageable) {
        Page<Movie> moviePage = movieRepository.findAll(pageable);
        return moviePage.map(MovieDTO::new);
    }

    public SuccessResponse deleteMovies(List<String> imdbIDs) {
        List<Movie> movies = movieRepository.findByImdbIDIn(imdbIDs);

        if (!movies.isEmpty()) {
            movieRepository.deleteAll(movies);
            return new SuccessResponse<>("Movies deleted successfully", movies.size());
        }
        return new SuccessResponse<>("No movies found to delete", movies.size());
    }
    public SuccessResponse<OMDbResponseDTO> searchMovieByImdb(String imdb) {
        try {
            String url = String.format(imdbUrl, imdb, apiKey);
            ResponseEntity<OMDbResponseDTO > response = restTemplate.getForEntity(url, OMDbResponseDTO .class);
            OMDbResponseDTO  movie = Optional.ofNullable(response.getBody())
                    .orElseThrow(() -> new ResourceNotFoundException("Movie not found with imdbID: " + imdb));

            return new SuccessResponse<>("Movie retrieved successfully", movie);

        } catch (Exception ex) {
            throw new ResourceNotFoundException("Error fetching movie: " + ex.getMessage());
        }
    }
    public SuccessResponse<List<MovieDTO>> searchMovie(String imdbID,String title,String year,String type) {
        try {
            List<Movie> movies = movieRepository.searchMovies(imdbID, title, year, type);
            List<MovieDTO> movieDTOs = movies.stream()
                    .map(MovieDTO::new)
                    .collect(Collectors.toList());
            return new SuccessResponse<>("Movies retrieved successfully", movieDTOs);

        } catch (Exception ex) {
            throw new ResourceNotFoundException("Error fetching movies: " + ex.getMessage());
        }
    }

}
