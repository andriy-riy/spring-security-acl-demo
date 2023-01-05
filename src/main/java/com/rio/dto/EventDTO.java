package com.rio.dto;

import java.time.LocalDateTime;
import java.util.List;

public record EventDTO(
        Long id,
        String title,
        String description,
        LocalDateTime startDateTime,
        LocalDateTime endDateTime,
        List<UserDTO> participants
) {
}