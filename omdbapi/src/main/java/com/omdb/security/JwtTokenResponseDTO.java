package com.omdb.security;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
public class JwtTokenResponseDTO {
    private String sessionToken;
}
