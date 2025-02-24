package com.omdb.controller;

import com.omdb.dto.MovieDTO;
import com.omdb.dto.OMDbResponseDTO;
import com.omdb.response.SuccessResponse;
import com.omdb.service.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins ="http://localhost:4200")
public class MovieController {

    @Autowired
    private MovieService movieService;

    @GetMapping("/movies/search")
    @PreAuthorize("hasAuthority('ADMIN')")
    @CrossOrigin(origins = "http://localhost:4200")
    public SuccessResponse<List<MovieDTO>> searchMovies(@RequestParam String query, @RequestParam int page) throws Exception {
        return movieService.searchMovies(query, page);
    }

    @PostMapping("/movies/save")
    @PreAuthorize("hasAuthority('ADMIN')")
    public SuccessResponse saveMovies(@RequestBody List<MovieDTO> movies) {

        return movieService.saveMoviesInBatches(movies);
    }

    @GetMapping("/movies/movies")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    public SuccessResponse<List<MovieDTO>> getMovies(@RequestParam(defaultValue = "0") int page) {
        Pageable pageable = PageRequest.of(page, 10);
        Page<MovieDTO> movies = movieService.getAllMovies(pageable);
        return new SuccessResponse<>("Movies retrieved successfully", movies.getContent());
    }

    @DeleteMapping("/movies/delete")
    @PreAuthorize("hasAuthority('ADMIN')")
    public SuccessResponse deleteMovies(@RequestBody List<String> ids) {
        return movieService.deleteMovies(ids);
    }

    @GetMapping("/movies/imdb")
    @PreAuthorize("hasAuthority('USER')")
    @CrossOrigin(origins = "http://localhost:4200")
    public SuccessResponse<OMDbResponseDTO> searchMovieByImdb(@RequestParam String imdb) throws Exception {
        return movieService.searchMovieByImdb(imdb);
    }

    @GetMapping("/search")
    @PreAuthorize("hasAuthority('USER')")
    public SuccessResponse<List<MovieDTO>> searchMovies(
            @RequestParam(required = false) String imdbID,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String year,
            @RequestParam(required = false) String type) {

        return movieService.searchMovie(imdbID, title, year, type);
    }
}
