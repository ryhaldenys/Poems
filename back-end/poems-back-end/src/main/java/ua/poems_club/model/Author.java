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

    private String imageName;

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

    public void removeAllMyLikes(Set<Poem> myLikes) {
        myLikes.forEach(m->m.getLikes().remove(this));
        this.myLikes.removeAll(myLikes);

    }

    public void removeAllPoems(List<Poem> poems) {
        this.poems.removeAll(poems);
        poems.forEach(m->m.setAuthor(null));
    }

    public enum Status{
        ACTIVE,BLOCKED
    }

    @Setter(AccessLevel.PRIVATE)
    @OneToMany(orphanRemoval = true,mappedBy = "author",cascade = {CascadeType.PERSIST,CascadeType.REMOVE})
    private List<Poem> poems = new ArrayList<>();

    @Setter(AccessLevel.PRIVATE)
    @ManyToMany(mappedBy = "subscriptions",cascade = {CascadeType.PERSIST,CascadeType.REMOVE})
//    @JoinTable(name = "user_subscribers",
//            joinColumns = @JoinColumn(name = "channel_id"),
//            inverseJoinColumns = @JoinColumn(name = "subscriber_id")
//    )
    private Set<Author> subscribers = new HashSet<>();

    @Setter(AccessLevel.PRIVATE)
    @ManyToMany(cascade = CascadeType.REMOVE)
    @JoinTable(name = "user_subscribers",
            joinColumns = @JoinColumn(name = "subscriber_id"),
            inverseJoinColumns = @JoinColumn(name = "channel_id")
    )
    private Set<Author> subscriptions = new HashSet<>();


    @ManyToMany(cascade = CascadeType.REMOVE,mappedBy = "likes")
    private Set<Poem> myLikes = new HashSet<>();

    public void addPoem(Poem poem){
        poems.add(poem);
        poem.setAuthor(this);
    }

    public void removeAllSubscribers(Set<Author>authors){
        authors.forEach(a->a.getSubscriptions().remove(this));
        subscribers.removeAll(authors);
    }


    public void removeAllSubscriptions(Set<Author>authors){
        authors.forEach(a->a.getSubscribers().remove(this));
        subscriptions.removeAll(authors);
    }

    public void addSubscriber(Author author){
        subscribers.add(author);
        author.getSubscriptions().add(this);
    }

    public void addSubscription(Author author){
        this.subscriptions.add(author);
        author.getSubscribers().add(this);
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
                ", imageUrl='" + imageName + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}