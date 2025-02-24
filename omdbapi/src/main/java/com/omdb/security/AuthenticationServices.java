package com.omdb.security;


import com.omdb.exception.ResourceNotFoundException;
import com.omdb.model.AppUser;
import com.omdb.model.TokenInfo;
import com.omdb.repository.AppUserRepository;
import com.omdb.response.SuccessResponse;
import com.omdb.service.TokenInfoServices;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@Log4j2
public class AuthenticationServices {

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private TokenInfoServices tokenInfoServices;
    @Autowired
    private JwtTokenUtils jwtTokenUtils;
    @Autowired
    private AppUserRepository appUserRepository;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    public SuccessResponse<JwtTokenResponseDTO> login(String userName, String password) {
        log.info("Attempting to authenticate user: " + userName);
        Optional<AppUser> userOptional = appUserRepository.findByUserName(userName);
        if (userOptional.isEmpty()) {
            throw new ResourceNotFoundException("Invalid username or password");
        }

        /**   Password Validation */
        if (!passwordEncoder.matches(password, userOptional.get().getPassword())) {
            throw new ResourceNotFoundException("Invalid username or password");
        }
        /** Authentication using username password authentication code */
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(userName, password));

        log.info("Valid username and password");

        /**  Get user details after authentication */

        AppUserDatiles appUserDatiles = (AppUserDatiles) authentication.getPrincipal();
        String role = appUserDatiles.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("ROLE_USER");
        /** Creation of token after validation */
        TokenInfo tokenInfo = createLoginToken(userName, appUserDatiles.getId(),role);

        /** Check token validity */
        if (jwtTokenUtils.isTokenExpired(tokenInfo.getAccessToken())) {
            throw new ResourceNotFoundException("Access token has expired");
        }

        /** Return the result in the form of SuccessResponse */
        return new SuccessResponse<>("Login successful", new JwtTokenResponseDTO(tokenInfo.getAccessToken()));

    }
    public TokenInfo createLoginToken(String userName, Long userId, String role) {

        String accessTokenId = UUID.randomUUID().toString();
        log.info("Access token create " + accessTokenId);
        String accessToken = jwtTokenUtils.generateToken(userName, accessTokenId, false, role,userId);
        log.info("Access token created " + accessTokenId);
        String refreshTokenId = UUID.randomUUID().toString();
        log.info("Refresh token create " + refreshTokenId);
        String refreshToken = jwtTokenUtils.generateToken(userName, refreshTokenId, true, role, userId);
        log.info("Refresh token created " + refreshTokenId);

        TokenInfo tokenInfo = new TokenInfo(accessToken, refreshToken);
        tokenInfo.setAppUser(new AppUser(userId));
        return tokenInfoServices.saveTokenInfo(tokenInfo);
    }

}
