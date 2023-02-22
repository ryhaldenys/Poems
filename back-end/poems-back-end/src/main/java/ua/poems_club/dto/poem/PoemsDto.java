package ua.poems_club.dto.poem;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
@EqualsAndHashCode(of = "{id,name,text,authorId}")
public class PoemsDto{
        private final Long id;
        private final String name;
        private final String text;
        private final Long authorId;
        private final String authorName;
        private final Long amountLikes;
        private final boolean isLike;

    public PoemsDto(Long id, String name, String text, Long authorId, String authorName, Long amountLikes, long countLikes) {
        this.id = id;
        this.name = name;
        this.text = text;
        this.authorId = authorId;
        this.authorName = authorName;
        this.amountLikes = amountLikes;
        this.isLike = countLikes > 0;
    }
}
