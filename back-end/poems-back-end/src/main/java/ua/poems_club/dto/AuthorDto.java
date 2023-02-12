package ua.poems_club.dto;

public record AuthorDto(Long id, String fullName,String description, String email,
                        String imageUrl,Long amountPoems,Long amountSubscribers,
                        Long amountSubscriptions,Long amountLikes) {
}
