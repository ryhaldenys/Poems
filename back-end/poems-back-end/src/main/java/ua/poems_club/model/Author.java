package ua.poems_club.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.NaturalId;

import java.util.ArrayList;
import java.util.List;

import static ua.poems_club.model.Author.Role.USER;
import static ua.poems_club.model.Author.Status.*;

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

    @Enumerated(EnumType.STRING)
    private Role role = USER;

    public enum Role{
        USER
    }

    @Enumerated(EnumType.STRING)
    private Status status = ACTIVE;

    public enum Status{
        ACTIVE,BLOCKED
    }

    @Setter(AccessLevel.PRIVATE)
    @OneToMany(orphanRemoval = true,mappedBy = "author",cascade = {CascadeType.PERSIST,CascadeType.REMOVE})
    private List<Poem> poems = new ArrayList<>();

    @Setter(AccessLevel.PRIVATE)
    @ManyToMany
    @JoinTable(name = "user_subscribers",
        joinColumns = @JoinColumn(name = "channel_id"),
        inverseJoinColumns = @JoinColumn(name = "subscriber_id")
    )
    private List<Author> subscribers = new ArrayList<>();

    @Setter(AccessLevel.PRIVATE)
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

    public void addAllSubscribers(List<Author>subscribers){
        this.subscribers = subscribers;
        subscribers.forEach(s->s.getSubscriptions().add(this));
    }

    public void addSubscription(Author author){
        subscriptions.add(author);
        author.getSubscribers().add(this);
    }


    public void addAllSubscriptions(List<Author>subscriptions){
        this.subscriptions = subscriptions;
        subscribers.forEach(s->s.getSubscribers().add(this));
    }

    public void addAllLikes(List<Poem>likes){
        this.myLikes = likes;
        likes.forEach(l->l.getLikes().add(this));
    }

}