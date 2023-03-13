package ua.poems_club.generator;

import ua.poems_club.builder.PoemBuilder;
import ua.poems_club.model.Poem;

import java.util.ArrayList;
import java.util.List;

public class PoemGenerator {

    private static long identifier;

    public static Poem generatePoemWithoutId(){
        return generateWithoutId();
    }

    private static Poem generateWithoutId() {
        identifier++;
        return PoemBuilder.builder()
                .name("name"+identifier)
                .text("text"+identifier)
                .status(Poem.Status.PUBLIC)
                .build();
    }

    public static Poem generatePoemWithId(){
        return generateWithId();
    }

    private static Poem generateWithId() {
        identifier++;
        return PoemBuilder.builder()
                .id(identifier)
                .name("name"+identifier)
                .text("text"+identifier)
                .status(Poem.Status.PUBLIC)
                .build();
    }

    public static List<Poem> generatePoemsWithoutId(int count){
        List<Poem> poems = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            var poem = generateWithoutId();
            poems.add(poem);
        }
        return poems;
    }

    public static List<Poem> generatePoemsWithId(int count){
        List<Poem> poems = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            var poem = generateWithId();
            poems.add(poem);
        }
        return poems;
    }
}
