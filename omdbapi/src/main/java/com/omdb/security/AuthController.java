package com.omdb.security;

import com.omdb.response.SuccessResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api")
public class AuthController {
    @Autowired
    private AuthenticationServices authenticationServices;
    @PostMapping("/v1/auth/login")
    public SuccessResponse<JwtTokenResponseDTO> login(@Valid @RequestBody LoginRequest loginRequest) {
        return authenticationServices.login(loginRequest.getUserName(), loginRequest.getPassword());

    }

}