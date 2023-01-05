package com.rio.dto;

import java.time.LocalDateTime;
import java.util.List;

public record CreateUpdateEventDTO(
        String title,
        String description,
        LocalDateTime startDateTime,
        LocalDateTime endDateTime,
        List<Long> participantIds
) {
}