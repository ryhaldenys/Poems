package ua.poems_club.service;

import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import ua.poems_club.builder.AuthorBuilder;
import ua.poems_club.dto.CreateAuthorDto;
import ua.poems_club.dto.PasswordDto;
import ua.poems_club.dto.UpdateAuthorDto;
import ua.poems_club.exception.AuthorAlreadyExist;
import ua.poems_club.exception.NotFoundException;
import ua.poems_club.generator.AuthorGenerator;
import ua.poems_club.model.Author;
import ua.poems_club.repository.AuthorRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@RequiredArgsConstructor
@ActiveProfiles(profiles = "test")
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
        assertThatException()
                .isThrownBy(()-> authorService.getAllAuthors(pageable));

    }

    @Test
    void getAllAuthorsTest(){
        var author = authors.get(0);
        var pageable = Mockito.any(Pageable.class);
        var authors = authorService.getAllAuthors(pageable).getContent();

        assertThat(authors.get(0).id()).isEqualTo(author.getId());

    }

    @Test
    void getAuthorByIdTest(){
        var author = authors.get(2);
        var foundAuthor = authorService.getAuthorById(author.getId());

        assertThat(foundAuthor.id()).isEqualTo(author.getId());

    }

    @Test
    void getAuthorByIdWhenUserIsAbsentTest(){
        assertThatThrownBy(()->authorService.getAuthorById(100042L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void createAccountTest(){
        var newAuthor = new CreateAuthorDto("fullName","email","new");

        var authorId = authorService.createAuthor(newAuthor);

        System.out.println(authorId);
        assertThat(authorId).isNotNull();
    }

    @Test
    void createAccountWithEmailWhichAlreadyExistTest(){
        var email = authors.get(0).getEmail();
        var newAuthor = new CreateAuthorDto("fullName",email,"new");

        assertThatThrownBy(()->authorService.createAuthor(newAuthor))
                .isInstanceOf(AuthorAlreadyExist.class)
                .hasMessage("Author with email: "+email+", already exist");
    }

    @Test
    void createAccountWithFullNameWhichAlreadyExistTest(){
        var fullName = authors.get(0).getFullName();
        var newAuthor = new CreateAuthorDto(fullName,"new","new");

        assertThatThrownBy(()->authorService.createAuthor(newAuthor))
                .isInstanceOf(AuthorAlreadyExist.class)
                .hasMessage("Author with full name: "+fullName+", already exist");
    }


    @Test
    void updateAuthorTest(){
        var id = authors.get(0).getId();
        var updateAuthorDto = new UpdateAuthorDto("Denys Ryhal","new@gmail.com","hello");
        authorService.updateAuthor(id,updateAuthorDto);

        var author = authorRepository.findById(id)
                .orElseThrow();

        assertThat(author.getFullName()).isEqualTo(updateAuthorDto.fullName());
        assertThat(author.getEmail()).isEqualTo(updateAuthorDto.email());
        assertThat(author.getDescription()).isEqualTo(updateAuthorDto.description());
    }

    @Test
    void updateAbsentAuthorTest(){
        var id = 121312423L;
        var updateAuthorDto = new UpdateAuthorDto("Denys Denys","new@gmail.com","hello");

        assertThatThrownBy(()->authorService.updateAuthor(id,updateAuthorDto))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void updateUserWithEmailWhichAlreadyExistTest(){
        var id = authors.get(0).getId();
        var email = authors.get(1).getEmail();
        var updateAuthorDto = new UpdateAuthorDto("Denys Denys",email,"hello");

        assertThatThrownBy(()->authorService.updateAuthor(id,updateAuthorDto))
                .isInstanceOf(AuthorAlreadyExist.class);
    }

    @Test
    void updateUserWithFullNameWhichAlreadyExistTest(){
        var id = authors.get(0).getId();
        var fullName = authors.get(1).getFullName();
        var updateAuthorDto = new UpdateAuthorDto(fullName,"new@gmail.com","hello");

        assertThatThrownBy(()->authorService.updateAuthor(id,updateAuthorDto))
                .isInstanceOf(AuthorAlreadyExist.class);
    }


    @Test
    void updatePasswordTest(){
        var author = authors.get(1);
        var password = new PasswordDto(author.getPassword(),"newpassword");
        authorService.updateAuthorPassword(author.getId(),password);

        var foundAuthor = authorRepository.findById(author.getId())
                        .orElseThrow();

        assertThat(foundAuthor.getPassword())
                .isEqualTo(password.newPassword());
    }

    @AfterEach
    void tearDown() {
        authorRepository.deleteAll();
    }
}
