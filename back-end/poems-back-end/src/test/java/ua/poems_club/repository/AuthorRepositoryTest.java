package ua.poems_club.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
    void findAllAuthorsTest(){
        author = authors.get(0);
        var currentUser = authors.get(1);
        var pageable = Pageable.unpaged();
        var authorsPage = authorRepository.findAllAuthors(currentUser.getId(),pageable);
        var authors = authorsPage.getContent();
        AuthorsDto authorsDto = new AuthorsDto(author.getId(),author.getFullName(),author.getDescription(), author.getImageName(),
                (long)author.getSubscribers().size(), (long)author.getPoems().size(),false);

        assertThat(authors.get(0)).isEqualTo(authorsDto);
    }


    @Test
    void findAuthorByIdTest(){
        author = authors.get(3);

        var foundAuthor = authorRepository.findAuthorById(author.getId())
                .orElseThrow();

        assertThat(foundAuthor.getId()).isEqualTo(author.getId());
        assertThat(foundAuthor.getFullName()).isEqualTo(author.getFullName());
    }

    @Test
    void findAuthorFetchSubscriptionsByIdTest(){
        author = authors.get(0);

        var foundAuthor = authorRepository.findAuthorFetchSubscriptions(author.getId())
                .orElseThrow();

        assertThat(foundAuthor.getId()).isEqualTo(author.getId());
        assertThat(foundAuthor.getSubscriptions().contains(authors.get(1))).isTrue();
        assertThat(foundAuthor.getSubscriptions().contains(authors.get(2))).isTrue();
    }

    @Test
    void findAuthorFetchSubscribersByIdTest(){
        author = authors.get(1);

        var foundAuthor = authorRepository.findAuthorFetchSubscribers(author.getId())
                .orElseThrow();

        assertThat(foundAuthor.getId()).isEqualTo(author.getId());
        assertThat(foundAuthor.getSubscribers().contains(authors.get(0))).isTrue();
    }


    @Test
    void findAllAuthorSubscriptions(){
        author = authors.get(0);
        var subscriptions = authorRepository
                .findAllSubscriptions(author.getId(),Pageable.unpaged())
                .getContent();

        var firstAuthorSubscription = authors.get(1);
        var secondAuthorSubscription = authors.get(2);

        assertThat(subscriptions.get(0).getFullName())
                .isEqualTo(firstAuthorSubscription.getFullName());

        assertThat(subscriptions.get(1).getFullName())
                .isEqualTo(secondAuthorSubscription.getFullName());
    }


    @Test
    void findAllAuthorSubscribers(){
        author = authors.get(1);
        var subscribers = authorRepository
                .findAllSubscribers(author.getId(),Pageable.unpaged())
                .getContent();

        var authorSubscribers = authors.get(0);

        assertThat(subscribers.get(0).getFullName())
                .isEqualTo(authorSubscribers.getFullName());
    }
}

