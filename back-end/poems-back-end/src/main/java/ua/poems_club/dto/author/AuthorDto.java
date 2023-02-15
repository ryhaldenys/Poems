package ua.poems_club.dto.author;

public record AuthorDto(Long id, String fullName,String description, String email,
                        String imageUrl,Long amountPoems,Long amountSubscribers,
                        Long amountSubscriptions,Long amountLikes) {
}
