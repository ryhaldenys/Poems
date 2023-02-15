package ua.poems_club.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ua.poems_club.generator.AuthorGenerator;
import ua.poems_club.generator.PoemGenerator;
import ua.poems_club.model.Author;
import ua.poems_club.model.Poem;

import java.util.List;

@DataJpaTest
public class PoemRepositoryTest {
    @Autowired
    private PoemRepository poemRepository;

    private List<Author> authors;
    private List<Poem> poems;
    @Autowired
    private AuthorRepository authorRepository;

    @BeforeEach
    void setUp() {
        addDataToDB();
    }

    @Test
    void getAllPoems(){

    }


    private void addDataToDB(){
        authors = AuthorGenerator.generateAuthorsWithoutId(5);
        poems = PoemGenerator.generatePoemsWithoutId(5);

        for (int i = 0; i < authors.size(); i++) {
            poems.get(i).addAuthor(authors.get(i));
        }
        authorRepository.saveAll(authors);
        authors.get(0).addAllLikes(poems);
        authors.get(0).addSubscriber(authors.get(1));
        authors.get(0).addSubscriber(authors.get(2));
        authors.get(3).addSubscriber(authors.get(0));
    }
}
