package ua.poems_club.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.test.web.servlet.MockMvc;

import ua.poems_club.dto.AuthorDto;
import ua.poems_club.dto.AuthorsDto;

import ua.poems_club.dto.CreateAuthorDto;
import ua.poems_club.exception.AuthorAlreadyExist;
import ua.poems_club.exception.NotFoundException;

import ua.poems_club.generator.AuthorGenerator;
import ua.poems_club.model.Author;
import ua.poems_club.service.AuthorService;

import java.util.List;


import static org.assertj.core.api.Assertions.*;
import static org.hamcrest.core.Is.*;
import static org.mockito.Mockito.*;

import static org.springframework.http.MediaType.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthorController.class)
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
                ,a.getImageUrl(), (long) a.getSubscribers().size(), (long) a.getPoems().size()))
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
                .andExpect(result -> assertThat(result.getResponse().getRedirectedUrl())
                        .isEqualTo("http://localhost/api/authors/"+author.getId())
                );
    }

    @Test
    @SneakyThrows
    void saveUserWithEmailWhichAlreadyExist(){
        var author = authors.get(2);
        var authorDto = new CreateAuthorDto(author.getFullName(), author.getEmail(), author.getPassword());
        when(service.createAuthor(authorDto))
                .thenThrow(AuthorAlreadyExist.class);

        mockMvc.perform(put("/api/authors/"+author.getId())
                        .contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertThat(result.getResolvedException())
                        .isInstanceOf(AuthorAlreadyExist.class)
                );
    }

    @Test
    @SneakyThrows
    void updateUserTest(){
        var author = authors.get(2);

        mockMvc.perform(put("/api/authors/"+author.getId())
                        .contentType(APPLICATION_JSON)
                        .content(mapObjectToString(author)))
                .andDo(print())
                .andExpect(status().isNoContent())
                .andExpect(result -> assertThat(result.getResponse().getRedirectedUrl())
                        .isEqualTo("http://localhost/api/authors/"+author.getId())
                );
    }


    @SneakyThrows
    private<T> String mapObjectToString(T author){
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(author);
    }
}
