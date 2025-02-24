package com.omdb.service;


import com.omdb.model.TokenInfo;
import com.omdb.repository.TokenInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TokenInfoServices {
    @Autowired
    private TokenInfoRepository tokenInfoRepository;

    public TokenInfo findByRefreshToken(String refreshToken){
        return tokenInfoRepository.findByRefreshToken(refreshToken);
    }
    public TokenInfo saveTokenInfo(TokenInfo tokenInfo){
        return   tokenInfoRepository.save(tokenInfo);
    }

}