package ua.poems_club.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import org.springframework.data.domain.Pageable;
import ua.poems_club.builder.AuthorBuilder;
import ua.poems_club.dto.AuthorsDto;
import ua.poems_club.model.Author;

@DataJpaTest
public class AuthorRepositoryTest {
    @Autowired
    private AuthorRepository authorRepository;
    private Author author;
    @BeforeEach
    void setUp() {
        author = AuthorBuilder.builder().email("email").fullName("fullName")
                .password("password")
                .imageUrl("url")
                .description("description")
                .build();
        authorRepository.save(author);
    }

    @Test
    void findAllAuthors(){
        var pageable = Mockito.any(Pageable.class);
        var authorsPage = authorRepository.findAllAuthors(pageable);
        var authors = authorsPage.getContent();
        AuthorsDto authorsDto = new AuthorsDto(author.getId(),author.getFullName(),author.getDescription(),
                (long)author.getSubscribers().size(), (long)author.getPoems().size());

        Assertions.assertThat(authors.get(0)).isEqualTo(authorsDto);
    }
}
