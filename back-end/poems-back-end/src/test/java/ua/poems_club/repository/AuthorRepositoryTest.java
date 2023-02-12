package ua.poems_club.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import org.springframework.data.domain.Pageable;
import ua.poems_club.dto.AuthorsDto;
import ua.poems_club.model.Author;


import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static ua.poems_club.generator.AuthorGenerator.*;

@DataJpaTest
public class AuthorRepositoryTest {
    @Autowired
    private AuthorRepository authorRepository;
    private Author author;
    private List<Author> authors;

    @BeforeEach
    void setUp() {
        authors = generateAuthorsWithoutId(5);
        authorRepository.saveAll(authors);
    }

    @Test
    void findAllAuthors(){
        author = authors.get(0);

        var pageable = Mockito.any(Pageable.class);
        var authorsPage = authorRepository.findAllAuthors(pageable);
        var authors = authorsPage.getContent();
        AuthorsDto authorsDto = new AuthorsDto(author.getId(),author.getFullName(),author.getDescription(), author.getImageUrl(),
                (long)author.getSubscribers().size(), (long)author.getPoems().size());

        assertThat(authors.get(0)).isEqualTo(authorsDto);
    }


    @Test
    void findAuthorById(){
        author = authors.get(3);

        var foundAuthor = authorRepository.findAuthorById(author.getId())
                .orElseThrow();

        assertThat(foundAuthor.id()).isEqualTo(author.getId());
        assertThat(foundAuthor.fullName()).isEqualTo(author.getFullName());
    }
}
