package ua.poems_club.dto.poem;

public record PoemsDto(Long id, String name, String text,Long authorId,String authorName,Long amountLikes, boolean isLike) {
}
