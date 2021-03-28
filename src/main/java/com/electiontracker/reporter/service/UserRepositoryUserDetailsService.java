package com.electiontracker.reporter.service;

import com.electiontracker.reporter.entities.UserAttributes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Optional;

@Service
public class UserRepositoryUserDetailsService implements UserDetailsService {

    @Autowired BuildUserDataFromFile buildUserDataFromFile;
    private List<UserAttributes> userAttributeList;

    @PostConstruct
    public void initialize() {
        userAttributeList = buildUserDataFromFile.getJsonUserListFromFile();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserAttributes> userAttributes = userAttributeList
                .stream()
                .filter(a->a.getUsername().equalsIgnoreCase(username))
                .findFirst();
        UserAttributes results = Optional.ofNullable(userAttributes)
                .get().orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return results;
    }
}
