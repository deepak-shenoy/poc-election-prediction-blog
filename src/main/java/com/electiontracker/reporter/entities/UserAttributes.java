package com.electiontracker.reporter.entities;

import lombok.Data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Data
public class UserAttributes implements UserDetails {

    private List<String> authorities = new ArrayList<>();
    private String password;
    private String username;
    private boolean accountNonExpired;
    private boolean accountNonLocked;
    private boolean credentialsNonExpired;
    private boolean enabled;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> authoritiesList = new ArrayList<>();
        authorities.forEach(authority -> {
            GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(authority);
            authoritiesList.add(new SimpleGrantedAuthority(authority));
        });
        return authoritiesList;
    }

}
