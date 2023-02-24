package ua.poems_club.dto.author;

import lombok.Getter;

@Getter
public class AuthorDto {
    private final Long id;
    private final String fullName;
    private final String description;
    private final String email;
    private  String imagePath;
    private final Long amountPoems;
    private final Long amountSubscribers;
    private final Long amountSubscriptions;
    private final Long amountLikes;

    public AuthorDto(Long id, String fullName, String description, String email,
                     String imagePath, Long amountPoems, Long amountSubscribers,
                     Long amountSubscriptions, Long amountLikes) {
        this.id = id;
        this.fullName = fullName;
        this.description = description;
        this.email = email;
        this.imagePath = imagePath;
        this.amountPoems = amountPoems;
        this.amountSubscribers = amountSubscribers;
        this.amountSubscriptions = amountSubscriptions;
        this.amountLikes = amountLikes;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    @Override
    public String toString() {
        return "AuthorDto[" +
                "id=" + id + ", " +
                "fullName=" + fullName + ", " +
                "description=" + description + ", " +
                "email=" + email + ", " +
                "imageUrl=" + imagePath + ", " +
                "amountPoems=" + amountPoems + ", " +
                "amountSubscribers=" + amountSubscribers + ", " +
                "amountSubscriptions=" + amountSubscriptions + ", " +
                "amountLikes=" + amountLikes + ']';
    }


}
