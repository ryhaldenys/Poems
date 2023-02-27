package ua.poems_club.generator;

import ua.poems_club.builder.AuthorBuilder;
import ua.poems_club.model.Author;

import java.util.ArrayList;
import java.util.List;

public class AuthorGenerator  {
    private static long identifier;

    public static List<Author> generateAuthorsWithoutId(int count) {
        return generateListWithoutId(count);
    }

    private static List<Author> generateListWithoutId(int count) {
        List<Author> authors = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            var author = generateWithoutId();
            authors.add(author);
        }
        return authors;
    }


    public static List<Author> generateAuthorsWithId(int count){
        return generateListWithId(count);
    }

    private static List<Author> generateListWithId(int count) {
        List<Author> authors = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            var author = generateWithId();
            authors.add(author);
        }
        return authors;
    }


    public static Author generateAuthorWithoutId() {
        return generateWithoutId();
    }

    private static Author generateWithoutId(){
        identifier++;
        return AuthorBuilder.builder().
                fullName("fullName"+identifier)
                .description("description"+identifier)
                .email("email"+identifier+"@gmail.com")
                .password("password"+identifier)
                .imageUrl("image"+identifier+".png")
                .build();
    }


    public static Author generateAuthorWithId() {
        return generateWithId();
    }

    private static Author generateWithId(){
        identifier++;
        return AuthorBuilder.builder()
                .id(identifier)
                .fullName("fullName"+identifier)
                .description("description"+identifier)
                .email("email"+identifier+"@gmail.com")
                .password("password"+identifier)
                .imageUrl("url"+identifier)
                .build();
    }

}
