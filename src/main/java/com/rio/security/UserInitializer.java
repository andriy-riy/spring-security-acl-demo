package com.rio.security;

import com.rio.entity.User;
import com.rio.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Month;

@Component
@RequiredArgsConstructor
public class UserInitializer {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @EventListener(value = ApplicationStartedEvent.class)
    public void initUsers() {
        createUserIfNotExists("vasily@gmail.com", "vasily", "Vasily", "Vasily", LocalDate.of(1997, Month.SEPTEMBER, 6));
        createUserIfNotExists("petro@gmail.com", "petro", "Petro", "Petro", LocalDate.of(1998, Month.JANUARY, 19));
    }

    private void createUserIfNotExists(String email, String password, String firstName, String lastName, LocalDate dateOfBirth) {
        if (!userRepository.existsByEmail(email)) {
            var user = new User();
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setDateOfBirth(dateOfBirth);
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(password));
            userRepository.save(user);
        }
    }
}
