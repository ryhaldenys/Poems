package ua.poems_club.security.dto;

public record AuthenticationResponseDto(Long id, String email, String token) {
}
