package ua.poems_club.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.NaturalId;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.time.LocalDateTime.*;
import static ua.poems_club.model.Author.Status.*;
import static ua.poems_club.model.Role.USER;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EqualsAndHashCode(of = "email")
@Table(name = "author")
public class Author {

    @Id
    @GeneratedValue
    private Long id;

    @NaturalId(mutable = true)
    @Column(nullable = false,unique = true)
    private String fullName;

    private String description;

    @Column(nullable = false)
    private String imageUrl;

    @Column(nullable = false,unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private LocalDateTime createdAt = now();

    @Enumerated(EnumType.STRING)
    private Role role = USER;

    @Enumerated(EnumType.STRING)
    private Status status = ACTIVE;

    public enum Status{
        ACTIVE,BLOCKED
    }

    @Setter(AccessLevel.PRIVATE)
    @OneToMany(orphanRemoval = true,mappedBy = "author",cascade = {CascadeType.PERSIST,CascadeType.REMOVE})
    private List<Poem> poems = new ArrayList<>();

    @Setter(AccessLevel.PRIVATE)
    @ManyToMany(cascade = {CascadeType.PERSIST,CascadeType.REMOVE},mappedBy = "subscriptions")
    private Set<Author> subscribers = new HashSet<>();

    @Setter(AccessLevel.PRIVATE)
    @ManyToMany(cascade = {CascadeType.PERSIST,CascadeType.REMOVE})
    @JoinTable(name = "user_subscribers",
            joinColumns = @JoinColumn(name = "subscriber_id"),
            inverseJoinColumns = @JoinColumn(name = "channel_id")
    )
    private Set<Author> subscriptions = new HashSet<>();


    @ManyToMany(cascade = {CascadeType.PERSIST,CascadeType.REMOVE},mappedBy = "likes")
    private Set<Poem> myLikes = new HashSet<>();

    public void addPoem(Poem poem){
        poems.add(poem);
        poem.setAuthor(this);
    }

    public void addAllPoems(List<Poem>poems){
        this.poems = poems;
        poems.forEach(p -> p.setAuthor(this));
    }

    public void addSubscriber(Author author){
        subscribers.add(author);
        author.getSubscriptions().add(this);
    }

    public void addAllSubscribers(Set<Author>subscribers){
        this.subscribers = subscribers;
        subscribers.forEach(s->s.getSubscriptions().add(this));
    }

    public void addSubscription(Author author){
        this.subscriptions.add(author);
        author.getSubscribers().add(this);
    }


    public void addAllSubscriptions(Set<Author>subscriptions){
        this.subscriptions = subscriptions;
        subscribers.forEach(s->s.getSubscribers().add(this));
    }

    public void addAllLikes(Set<Poem>likes){
        this.myLikes = likes;
        likes.forEach(l->l.getLikes().add(this));
    }

    public void addLike(Poem like){
        this.myLikes.add(like);
        like.getLikes().add(this);
    }

    public void removeSubscription(Author author){
        this.subscriptions.remove(author);
        author.getSubscribers().remove(this);
    }

    @Override
    public String toString() {
        return "Author{" +
                "id=" + id +
                ", fullName='" + fullName + '\'' +
                ", description='" + description + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}