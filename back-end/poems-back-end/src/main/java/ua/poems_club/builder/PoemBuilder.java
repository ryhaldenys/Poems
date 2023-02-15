package ua.poems_club.builder;

import ua.poems_club.model.Author;
import ua.poems_club.model.Poem;

import java.util.ArrayList;
import java.util.List;

public class PoemBuilder {
    private Long id;
    private String name;
    private String text;
    private final List<Author> likes = new ArrayList<>();

    public static PoemBuilder builder(){
        return new PoemBuilder();
    }

    public PoemBuilder id(Long id){
        this.id = id;
        return this;
    }

    public PoemBuilder name(String name){
        this.name = name;
        return this;
    }
    public PoemBuilder text(String text){
        this.text = text;
        return this;
    }

    public PoemBuilder like(Author like){
        this.likes.add(like);
        return this;
    }

    public Poem build(){
        return initializePoem();
    }

    private Poem initializePoem(){
        var poem = new Poem();
        poem.setId(id);
        poem.setName(name);
        poem.setText(text);
        //poem.addAuthor(author);
        poem.addAllLikes(likes);
        return poem;
    }

}
