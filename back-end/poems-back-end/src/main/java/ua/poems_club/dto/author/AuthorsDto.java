package ua.poems_club.dto.author;

public record AuthorsDto(Long id, String fullName, String description,String imageUrl,
                         Long amountSubscribers, Long amountPoems,boolean isSubscribe) {
}
