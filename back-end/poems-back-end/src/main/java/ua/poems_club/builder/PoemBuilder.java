package ua.poems_club.builder;

import ua.poems_club.model.Author;
import ua.poems_club.model.Poem;
import ua.poems_club.model.Poem.Status;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static java.time.LocalDateTime.*;
import static ua.poems_club.model.Poem.Status.*;

public class PoemBuilder {
    private Long id;
    private String name;
    private String text;
    private Status status = PRIVATE;
    private final Set<Author> likes = new HashSet<>();
    private LocalDateTime createdAt = now();
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

    public PoemBuilder status(Status status){
        this.status = status;
        return this;
    }

    public PoemBuilder createdAt(LocalDateTime createdAt){
        this.createdAt = createdAt;
        return this;
    }

    public Poem build(){
        return initializePoem();
    }

    private Poem initializePoem(){
        return new Poem(id,name,text,createdAt,status,null,likes);
    }

}
