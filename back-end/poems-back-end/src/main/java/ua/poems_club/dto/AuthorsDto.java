package ua.poems_club.dto;

public record AuthorsDto(Long id, String fullName, String description,
                         Long amountSubscribers, Long amountPoems) {
}
