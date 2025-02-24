package com.omdb.security;

import com.omdb.model.AppUser;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AppUserDatiles implements UserDetails {
    private Long id;
    private String username;
    private String password;
    private List<GrantedAuthority> grantedAuthorities;

    public AppUserDatiles(AppUser appUser) {
        super();
        this.id = appUser.getId();
        this.username = appUser.getUserName();
        this.password = appUser.getPassword();
        /** Initialize grantedAuthorities properly */
        this.grantedAuthorities = new ArrayList<>();
        if (appUser.getRoles() != null && !appUser.getRoles().isEmpty()) {
            appUser.getRoles().forEach(role -> {
                grantedAuthorities.add(new SimpleGrantedAuthority(role.getName()));
            });
        }
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return grantedAuthorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }
}
