package com.omdb.security;


import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.nio.charset.StandardCharsets;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.crypto.spec.SecretKeySpec;
import java.util.Calendar;
import java.util.Date;


@Log4j2
@Component
public class JwtTokenUtils {
    private static String tokenSecret;
    private static Long accessTokenValidity;
    private  static Long refreshTokenValidity;

    public JwtTokenUtils(@Value("${auth.secret}") String tokenSecret,
                         @Value("${auth.accessToken}") Long accessTokenValidity,
                         @Value("${auth.refresh.expiration}") Long refreshTokenValidity) {
        this.tokenSecret = tokenSecret;
        this.accessTokenValidity = accessTokenValidity;
        this.refreshTokenValidity = refreshTokenValidity;
    }

    public static String generateToken(String userName, String tokenId, boolean isRefresh, String role,Long userId) {
        if (tokenSecret.length() < 64) {
            throw new IllegalArgumentException("The tokenSecret must be at least 64 characters for HS512.");
        }

        Key key = new SecretKeySpec(tokenSecret.getBytes(), SignatureAlgorithm.HS512.getJcaName());

        return Jwts.builder()
                .setId(tokenId)
                .setSubject(userName)
                .setIssuedAt(new Date())
                .setIssuer("app")
                .setExpiration(colcTokenExpiration(isRefresh))
                .claim("created", Calendar.getInstance().getTime())
                .claim("role", role)
                .claim("userId", userId)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    public static Date colcTokenExpiration(boolean isRefresh) {
        long validity = isRefresh ? refreshTokenValidity : accessTokenValidity;
        return new Date(System.currentTimeMillis() + validity * 1000);
    }

    public static boolean isTokenValid(String token, AppUserDatiles appUserDatiles) {
        log.info("isTokenExpired" + isTokenExpired(token));
        String userName = getUsernameFromToken(token);
        log.info("Token User Name " + token);
        log.info("appUserDatiles.getUsername()" + appUserDatiles.getUsername());
        Boolean isUserNameEqual = userName.equalsIgnoreCase(appUserDatiles.getUsername());
        return (isUserNameEqual && !isTokenExpired(token));
    }

    public static  boolean isTokenExpired(String token) {
        Date expiration = getClaims(token).getExpiration();
        return expiration.before(new Date());
    }

    //get username with token
    public static String getUsernameFromToken(String token) {
        Claims claims = getClaims(token);
        return claims.getSubject();
    }
    public static Long getUserIdFromToken(String token) {
        Claims claims = getClaims(token);
        return claims.get("userId", Long.class);
    }

    private  static Claims getClaims(String token) {
        Key key = Keys.hmacShaKeyFor(tokenSecret.getBytes());

        return Jwts.parser()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public  boolean validateToken(String token) {
        try {
            Key key = Keys.hmacShaKeyFor(tokenSecret.getBytes(StandardCharsets.UTF_8));
            Jwts.parser()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);

            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT token");
            return false;
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT token");
            return false;
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT token");
            return false;
        } catch (IllegalArgumentException e) {
            log.info("JWT claims string is empty.");
            return false;
        }
    }

}
