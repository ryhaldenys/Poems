package ua.poems_club.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "poem",indexes = @Index(name="poem_author_id_idx",columnList = "author_id"))
public class Poem {
    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String text;

    @JsonIgnore
    @ManyToOne(optional = false)
    @JoinColumn(name = "author_id")
    private Author author;

    @ManyToMany
    @JoinTable(name = "poem_likes",
    joinColumns = @JoinColumn(name = "poem_id"),
    inverseJoinColumns = @JoinColumn(name="user_id"))
    private List<Author> likes = new ArrayList<>();

    public void addAuthor(Author author){
        this.author = author;
        author.getPoems().add(this);
    }

    public void addAllLikes(List<Author> likes){
        this.likes = likes;
        likes.forEach(l->l.getMyLikes().add(this));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Poem poem = (Poem) o;

        return this.id!=null && id.equals(poem.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
