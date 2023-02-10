package ua.poems_club.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.NaturalId;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@Entity
@EqualsAndHashCode(of = "email")
@Table(name = "author")
public class Author {

    @Id
    @GeneratedValue
    private Long id;

    @NaturalId
    @Column(nullable = false,unique = true)
    private String fullName;

    @Column(nullable = false,unique = true)
    private String imageUrl;

    @Column(nullable = false,unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Setter(AccessLevel.PRIVATE)
    @OneToMany(orphanRemoval = true,mappedBy = "author")
    private List<Poem> poems = new ArrayList<>();

    @ManyToMany
    @JoinTable(name = "user_subscribers",
        joinColumns = @JoinColumn(name = "channel_id"),
        inverseJoinColumns = @JoinColumn(name = "subscriber_id")
    )
    private List<Author> subscribers = new ArrayList<>();

    @ManyToMany
    @JoinTable(name = "user_subscribers",
            joinColumns = @JoinColumn(name = "subscriber_id"),
            inverseJoinColumns = @JoinColumn(name = "channel_id")
    )
    private List<Author> subscriptions = new ArrayList<>();

    @ManyToMany
    @JoinTable(name = "poem_likes",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name="poem_id"))
    private List<Poem> myLikes = new ArrayList<>();
}