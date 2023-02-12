package ua.poems_club.builder;

import ua.poems_club.model.Author;
import ua.poems_club.model.Poem;

import java.util.ArrayList;
import java.util.List;

import static ua.poems_club.model.Author.Role.USER;
import static ua.poems_club.model.Author.Status.ACTIVE;

public class AuthorBuilder {
    private Long id;
    private String fullName;
    private String description;
    private String imageUrl;
    private String email;
    private String password;
    private Author.Role role = USER;
    private Author.Status status = ACTIVE;
    private final List<Poem> poems = new ArrayList<>();
    private final List<Author> subscribers = new ArrayList<>();
    private final List<Author> subscriptions = new ArrayList<>();
    private final List<Poem> myLikes = new ArrayList<>();

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

    public AuthorBuilder role(Author.Role role){
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
        var author = new Author();
        author.setId(id);
        author.setFullName(fullName);
        author.setDescription(description);
        author.setImageUrl(imageUrl);
        author.setEmail(email);
        author.setPassword(password);
        author.setRole(role);
        author.setStatus(status);
        author.addAllPoems(poems);
        author.addAllSubscribers(subscribers);
        author.addAllSubscriptions(subscriptions);
        author.addAllLikes(myLikes);
        return author;
    }
}
