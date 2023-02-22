package ua.poems_club.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import org.springframework.data.domain.Pageable;
import ua.poems_club.dto.author.AuthorsDto;
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
        var author = authors.get(0);
        author.addSubscription(authors.get(1));
        author.addSubscription(authors.get(2));

    }

    @Test
    void findAllAuthors(){
        author = authors.get(0);
        var currentUser = authors.get(1);
        var pageable = Mockito.any(Pageable.class);
        var authorsPage = authorRepository.findAllAuthors(currentUser.getId(),pageable);
        var authors = authorsPage.getContent();
        AuthorsDto authorsDto = new AuthorsDto(author.getId(),author.getFullName(),author.getDescription(), author.getImageUrl(),
                (long)author.getSubscribers().size(), (long)author.getPoems().size(),false);

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

    @Test
    void findAuthorFetchSubscriptionsById(){
        author = authors.get(0);

        var foundAuthor = authorRepository.findAuthorFetchSubscriptions(author.getId())
                .orElseThrow();

        assertThat(foundAuthor.getId()).isEqualTo(author.getId());
        assertThat(foundAuthor.getSubscriptions().contains(authors.get(1))).isTrue();
        assertThat(foundAuthor.getSubscriptions().contains(authors.get(2))).isTrue();
    }

    @Test
    void findAuthorFetchSubscribersById(){
        author = authors.get(1);

        var foundAuthor = authorRepository.findAuthorFetchSubscribers(author.getId())
                .orElseThrow();

        assertThat(foundAuthor.getId()).isEqualTo(author.getId());
        assertThat(foundAuthor.getSubscribers().contains(authors.get(0))).isTrue();
    }


}

