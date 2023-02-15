package ua.poems_club.dto.poem;


public record PoemDto(Long id, String name, String text, Long authorId, String authorName) {
}
