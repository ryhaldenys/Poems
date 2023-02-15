package ua.poems_club.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import ua.poems_club.dto.author.*;
import ua.poems_club.exception.AuthorAlreadyExist;
import ua.poems_club.exception.IncorrectAuthorDetailsException;
import ua.poems_club.exception.NotFoundException;

import ua.poems_club.generator.AuthorGenerator;
import ua.poems_club.model.Author;
import ua.poems_club.service.AuthorService;

import java.util.List;
import java.util.Objects;


import static org.assertj.core.api.Assertions.*;
import static org.hamcrest.core.Is.*;
import static org.mockito.Mockito.*;

import static org.springframework.http.MediaType.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthorController.class)
@ActiveProfiles("test")
public class AuthorControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthorService service;

    private List<Author> authors;
    @BeforeEach
    void setUp() {
        authors = AuthorGenerator.generateAuthorsWithId(5);
    }

    @SneakyThrows
    @Test
    void getAllAuthorsTest(){
        var authorsDtos = authors.stream().map(a -> new AuthorsDto(a.getId(),a.getFullName(),a.getDescription()
                ,a.getImageUrl(), (long) a.getSubscribers().size(), (long) a.getPoems().size(),false))
                .toList();

        Page<AuthorsDto> page = new PageImpl<>(authorsDtos);

        when(service.getAllAuthors(any(Pageable.class)))
                .thenReturn(page);

        mockMvc.perform(get("/api/authors")
                        .contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].fullName", is(authorsDtos.get(0).fullName())))
                .andExpect(jsonPath("$.content[1].fullName", is(authorsDtos.get(1).fullName())));

    }

    @SneakyThrows
    @Test
    void getEmptyPageOfAuthorsTest(){
        when(service.getAllAuthors(any(Pageable.class)))
                .thenThrow(NotFoundException.class);

        mockMvc.perform(get("/api/authors")
                        .contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(result -> assertThat(result.getResolvedException())
                        .isInstanceOf(NotFoundException.class));

    }


    @Test
    @SneakyThrows
    void getAuthorByIdTest(){
        AuthorDto authorDto = new AuthorDto(1L,"fullName","desc",
                "email@gmail.com","url",1L,2L,4L,3L);

        when(service.getAuthorById(1L)).thenReturn(authorDto);

        mockMvc.perform(get("/api/authors/1")
                .contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName",is(authorDto.fullName())))
                .andExpect(jsonPath("$.email",is(authorDto.email())));
    }

    @Test
    @SneakyThrows
    void getAuthorByIdFromEmptyDBTest(){
        when(service.getAuthorById(1L)).thenThrow(NotFoundException.class);

        mockMvc.perform(get("/api/authors/1")
                        .contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(result -> assertThat(result.getResolvedException())
                        .isInstanceOf(NotFoundException.class)
                );
    }

    @Test
    @SneakyThrows
    void saveAuthorTest(){
        var author = authors.get(0);
        var authorDto = new CreateAuthorDto(author.getFullName(), author.getEmail(), author.getPassword());
        mockMvc.perform(post("/api/authors")
                        .contentType(APPLICATION_JSON)
                        .content(mapObjectToString(authorDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(result -> assertThat(Objects.requireNonNull(result.getResponse().getRedirectedUrl())
                        .contains("http://localhost/api/authors/"))
                        .isTrue()
                );
    }

    @Test
    @SneakyThrows
    void saveAuthorWithEmailWhichAlreadyExist(){
        var author = authors.get(2);
        var authorDto = new CreateAuthorDto(author.getFullName(), author.getEmail(), author.getPassword());
        when(service.createAuthor(authorDto))
                .thenThrow(AuthorAlreadyExist.class);

        mockMvc.perform(post("/api/authors")
                        .contentType(APPLICATION_JSON)
                        .content(mapObjectToString(authorDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertThat(result.getResolvedException())
                        .isInstanceOf(AuthorAlreadyExist.class)
                );
    }

    @Test
    @SneakyThrows
    void updateAuthorTest(){
        var author = authors.get(2);

        mockMvc.perform(put("/api/authors/"+author.getId())
                        .contentType(APPLICATION_JSON)
                        .content(mapObjectToString(author)))
                .andDo(print())
                .andExpect(status().isNoContent())
                .andExpect(result -> assertThat(Objects.requireNonNull(result.getResponse().getRedirectedUrl())
                        .contains("http://localhost/api/authors/"))
                        .isTrue()
                );
    }

    @Test
    @SneakyThrows
    void updateAuthorWithFullNameWhichAlreadyExist(){
        var author = authors.get(2);
        var authorDto = new UpdateAuthorDto(author.getFullName(), author.getEmail(), author.getDescription());

        BDDMockito.willThrow(AuthorAlreadyExist.class).given(service).updateAuthor(author.getId(),authorDto);

        mockMvc.perform(put("/api/authors/"+author.getId())
                        .contentType(APPLICATION_JSON)
                        .content(mapObjectToString(authorDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertThat(result.getResolvedException())
                        .isInstanceOf(AuthorAlreadyExist.class)
                );
    }


    @Test
    @SneakyThrows
    void updateAuthorPasswordTest(){
        var author = authors.get(2);

        var password = new PasswordDto("old","new");

        mockMvc.perform(patch("/api/authors/"+author.getId())
                        .contentType(APPLICATION_JSON)
                        .content(mapObjectToString(password)))
                .andDo(print())
                .andExpect(status().isNoContent())
                .andExpect(result -> assertThat(result.getResponse().getRedirectedUrl())
                        .isEqualTo("http://localhost/api/authors/"+author.getId())
                );
    }

    @Test
    @SneakyThrows
    void updateAuthorPasswordWithWrongOldIdTest(){
        var author = authors.get(2);

        var password = new PasswordDto("old","new");

        BDDMockito.willThrow(IncorrectAuthorDetailsException.class)
                .given(service).updateAuthorPassword(author.getId(),password);

        mockMvc.perform(patch("/api/authors/"+author.getId())
                        .contentType(APPLICATION_JSON)
                        .content(mapObjectToString(password)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertThat(result.getResolvedException())
                        .isInstanceOf(IncorrectAuthorDetailsException.class)
                );
    }

    @Test
    @SneakyThrows
    void deleteAuthorTest(){
        var author = authors.get(0);

        when(service.deleteAuthor(author.getId()))
                .thenReturn(author);

        when(service.deleteAuthor(1L)).thenReturn(author);

        mockMvc.perform(delete("/api/authors/1")
                        .contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName",is(author.getFullName())))
                .andExpect(jsonPath("$.email",is(author.getEmail())));
    }




    @SneakyThrows
    private<T> String mapObjectToString(T author){
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(author);
    }
}
