package ua.poems_club.dto.poem;


import ua.poems_club.model.Poem;

public record PoemDto(Long id, String name, String text, Poem.Status status, Long authorId, String authorName) {
}
