package ua.poems_club.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ua.poems_club.exception.NotFoundException;
import ua.poems_club.generator.PoemGenerator;
import ua.poems_club.model.Author;
import ua.poems_club.model.Poem;
import ua.poems_club.repository.AuthorRepository;

import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static ua.poems_club.generator.AuthorGenerator.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class AuthorPoemServiceTest {
    @Autowired
    private AuthorPoemService authorPoemService;

    @Autowired
    private AuthorRepository authorRepository;

    private List<Author> authors;
    private List<Poem> poems;
    private Author currentUser;

    @BeforeEach
    void setUp() {
        addDataToDB();
        currentUser = generateAuthorWithoutId();
    }

    @Test
    void getAllByAuthorIdTest(){
        var author = authors.get(0);
        var foundPoems = authorPoemService.getAllByAuthorId(author.getId(), currentUser.getId(), Pageable.unpaged()).getContent();

        assertThat(foundPoems.get(0).getId()).isEqualTo(author.getPoems().get(0).getId());
        assertThat(foundPoems.get(0).getAmountLikes()).isEqualTo(1L);
    }

    @Test
    void getAllByWrongAuthorIdTest(){

        assertThatThrownBy(()->authorPoemService.getAllByAuthorId(1000L, currentUser.getId(), Pageable.unpaged()))
                .isInstanceOf(NotFoundException.class);
    }

    private void addDataToDB(){
        authors = generateAuthorsWithoutId(5);
        poems = PoemGenerator.generatePoemsWithoutId(5);

        for (int i = 0; i < authors.size(); i++) {
            poems.get(i).addAuthor(authors.get(i));
        }
        authorRepository.saveAll(authors);


        authors.get(0).addAllLikes(new HashSet<>(poems));
        authors.get(0).addSubscriber(authors.get(1));
        authors.get(0).addSubscriber(authors.get(2));
        authors.get(3).addSubscriber(authors.get(0));
    }

}


