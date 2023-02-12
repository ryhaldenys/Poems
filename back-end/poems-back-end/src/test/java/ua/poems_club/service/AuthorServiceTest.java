package ua.poems_club.service;

import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import ua.poems_club.builder.AuthorBuilder;
import ua.poems_club.model.Author;
import ua.poems_club.repository.AuthorRepository;

@SpringBootTest
@RequiredArgsConstructor
public class AuthorServiceTest {
    @Autowired
    private AuthorService authorService;

    @Autowired
    private AuthorRepository authorRepository;

    @Test
    void getAllAuthorsFromEmptyTableTest(){
        var pageable = Mockito.any(Pageable.class);
        Assertions.assertThatException()
                .isThrownBy(()-> authorService.getAllAuthors(pageable));

    }

    @Test
    void getAllAuthorsTest(){
        Author author = AuthorBuilder.builder().email("email").fullName("fullName")
                .password("password")
                .imageUrl("url")
                .description("description")
                .build();
        authorRepository.save(author);

        var pageable = Mockito.any(Pageable.class);
        var authors = authorService.getAllAuthors(pageable).getContent();

        Assertions.assertThat(authors.get(0).id()).isEqualTo(author.getId());
        authorRepository.deleteAll();
    }

}
