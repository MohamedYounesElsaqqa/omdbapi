package com.omdb.controller;

import com.omdb.dto.RatingDTO;
import com.omdb.response.SuccessResponse;
import com.omdb.security.JwtTokenUtils;
import com.omdb.service.RatingService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("http://localhost:4200")
@RestController
@RequestMapping("/api")
public class RatingController {

    @Autowired
    private  RatingService ratingService;

    @PostMapping("/ratings/add")
    @PreAuthorize("hasAuthority('USER')")
    public SuccessResponse addRating(@RequestBody RatingDTO ratingDTO, HttpServletRequest request) {
        String token = request.getHeader("Authorization").replace("Bearer ", "");
        Long userId = JwtTokenUtils.getUserIdFromToken(token);

        ratingDTO.setUserId(userId);
            return ratingService.addRating(ratingDTO);
    }
}
