package ua.poems_club.builder;

import ua.poems_club.model.Author;
import ua.poems_club.model.Poem;
import ua.poems_club.model.Role;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static ua.poems_club.model.Author.Status.ACTIVE;
import static ua.poems_club.model.Role.USER;

public class AuthorBuilder {
    private Long id;
    private String fullName;
    private String description;
    private String imageUrl;
    private String email;
    private String password;
    private Role role = USER;
    private Author.Status status = ACTIVE;
    private final List<Poem> poems = new ArrayList<>();
    private final Set<Author> subscribers = new HashSet<>();
    private final Set<Author> subscriptions = new HashSet<>();
    private final Set<Poem> myLikes = new HashSet<>();
    private LocalDateTime createdAt = LocalDateTime.now();


    public static AuthorBuilder builder(){
        return new AuthorBuilder();
    }

    public AuthorBuilder id(Long id){
        this.id = id;
        return this;
    }

    public AuthorBuilder fullName(String fullName){
        this.fullName = fullName;
        return this;
    }

    public AuthorBuilder description(String description){
        this.description =description;
        return this;
    }

    public AuthorBuilder imageUrl(String imageUrl){
        this.imageUrl = imageUrl;
        return this;
    }

    public AuthorBuilder email(String email){
        this.email = email;
        return this;
    }

    public AuthorBuilder password(String password){
        this.password = password;
        return this;
    }

    public AuthorBuilder createdAt(LocalDateTime createdAt){
        this.createdAt = createdAt;
        return this;
    }

    public AuthorBuilder role(Role role){
        this.role =role;
        return this;
    }

    public AuthorBuilder status(Author.Status status){
        this.status = status;
        return this;
    }

    public AuthorBuilder poems(Poem poem){
        poems.add(poem);
        return this;
    }

    public AuthorBuilder subscribers(Author author){
        this.subscribers.add(author);
        return this;
    }

    public AuthorBuilder subscriptions(Author author){
        this.subscriptions.add(author);
        return this;
    }

    public AuthorBuilder authorLikes(Poem poem){
        this.myLikes.add(poem);
        return this;
    }

    public Author build(){
        return initializeAuthor();
    }


    private Author initializeAuthor(){
        return new Author(id,fullName,description,imageUrl,email,password,
                createdAt,role,status,poems,subscribers,subscriptions,myLikes);
    }
}
