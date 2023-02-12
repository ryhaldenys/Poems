package ua.poems_club.service;

import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import ua.poems_club.exception.NotFoundException;
import ua.poems_club.generator.AuthorGenerator;
import ua.poems_club.model.Author;
import ua.poems_club.repository.AuthorRepository;

import java.util.List;

@SpringBootTest
@RequiredArgsConstructor
public class AuthorServiceTest {
    @Autowired
    private AuthorService authorService;

    @Autowired
    private AuthorRepository authorRepository;
    private List<Author> authors;

    @BeforeEach
    void setUp() {
        authors = AuthorGenerator.generateAuthorsWithoutId(5);
        authorRepository.saveAll(authors);
    }

    @Test
    void getAllAuthorsFromEmptyTableTest(){
        authorRepository.deleteAll();
        var pageable = Mockito.any(Pageable.class);
        Assertions.assertThatException()
                .isThrownBy(()-> authorService.getAllAuthors(pageable));

    }

    @Test
    void getAllAuthorsTest(){
        var author = authors.get(0);
        var pageable = Mockito.any(Pageable.class);
        var authors = authorService.getAllAuthors(pageable).getContent();

        Assertions.assertThat(authors.get(0).id()).isEqualTo(author.getId());

    }

    @Test
    void getAuthorByIdTest(){
        var author = authors.get(2);
        var foundAuthor = authorService.getAuthorById(author.getId());

        Assertions.assertThat(foundAuthor.id()).isEqualTo(author.getId());

    }

    @Test
    void getAuthorByIdWhenUserIsAbsentTest(){
        Assertions.assertThatThrownBy(()->authorService.getAuthorById(100042L))
                .isInstanceOf(NotFoundException.class);
    }

    @AfterEach
    void tearDown() {
        authorRepository.deleteAll();
    }
}
