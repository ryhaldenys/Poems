package ua.poems_club.dto.author;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;


@Getter
@EqualsAndHashCode(of = "{id,fullName,imageUrl}")
@AllArgsConstructor
public class AuthorsDto{
        private Long id;
        private String fullName;
        private String description;
        private String imageUrl;
        private Long amountSubscribers;
        private Long amountPoems;
        private boolean isSubscribe;

    public AuthorsDto(Long id, String fullName, String description, String imageUrl, Long amountSubscribers, Long amountPoems, long amountOfSubscribers) {
        this.id = id;
        this.fullName = fullName;
        this.description = description;
        this.imageUrl = imageUrl;
        this.amountSubscribers = amountSubscribers;
        this.amountPoems = amountPoems;
        this.isSubscribe = amountOfSubscribers>0;
    }
}
