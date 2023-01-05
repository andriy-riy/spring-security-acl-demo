package com.rio.security;

import com.rio.entity.Event;
import com.rio.entity.User;
import com.rio.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class PermissionsService {

    private final UserRepository userRepository;

    @Transactional
    public boolean hasPermissions(Authentication authentication, Long eventId) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
        return user.getCreatedEvents().stream()
                .map(Event::getId)
                .anyMatch(id -> Objects.equals(eventId, id));
    }
}
