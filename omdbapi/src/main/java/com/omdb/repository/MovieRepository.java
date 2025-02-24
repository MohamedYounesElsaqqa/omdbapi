package com.omdb.repository;

import com.omdb.model.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {
    Optional<Movie> findByImdbID(String imdbID);
    List<Movie> findByImdbIDIn(List<String> imdbIDs);
    @Query(value = "SELECT * FROM postgres.fawry.movies " +
            "WHERE (:imdbID = '' OR imdbID = :imdbID) " +
            "AND (:title = '' OR title ILIKE '%' || :title || '%') " +
            "AND (:year = '' OR year = :year) " +
            "AND (:type = '' OR type = :type)", nativeQuery = true)
    List<Movie> searchMovies(@Param("imdbID") String imdbID,
                             @Param("title") String title,
                             @Param("year") String year,
                             @Param("type") String type);


}
