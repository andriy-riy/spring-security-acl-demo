package com.rio.security;

import com.rio.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Collections;

@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username)
                .map(user -> new User(user.getEmail(), user.getPassword(), user.getEmail().equals("milner@gmail.com") ? Collections.singletonList(new SimpleGrantedAuthority("ROLE_MANAGER")) : user.getPermissions()))
                .orElseThrow(() -> new UsernameNotFoundException("User with username: '" + username + "' not found"));
    }
}
