package com.omdb.service;

import com.omdb.model.AppUser;
import com.omdb.repository.AppUserRepository;
import com.omdb.security.AppUserDatiles;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AppUserServices implements UserDetailsService {
    @Autowired
    private  AppUserRepository appUserRepository;

    public List<AppUser> appUserList (){
       return appUserRepository.findAll();
    }

    public AppUser findById(Long id) {
        return appUserRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + id));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

       Optional<AppUser> appUser= appUserRepository.findByUserName(username);
       if(!appUser.isPresent()){
           throw new UsernameNotFoundException("User not found with username: " + username);
       }

        return new AppUserDatiles(appUser.get());

    }
}
