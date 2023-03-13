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
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import ua.poems_club.dto.author.*;
import ua.poems_club.dto.poem.PoemsDto;
import ua.poems_club.exception.AuthorAlreadyExist;
import ua.poems_club.exception.IncorrectAuthorDetailsException;
import ua.poems_club.exception.NotFoundException;

import ua.poems_club.generator.AuthorGenerator;
import ua.poems_club.model.Author;
import ua.poems_club.security.model.JwtTokenProvider;
import ua.poems_club.security.model.SecurityUser;
import ua.poems_club.security.service.AuthenticationService;
import ua.poems_club.service.AuthorService;

import java.util.List;
import java.util.Objects;


import static org.assertj.core.api.Assertions.*;
import static org.hamcrest.core.Is.*;
import static org.mockito.Mockito.*;

import static org.springframework.http.MediaType.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ua.poems_club.model.Poem.Status.*;

@WithMockUser
@WebMvcTest(AuthorController.class)
public class AuthorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthorService service;

    @MockBean
    private AuthenticationService authenticationService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    private List<Author> authors;
    private UserDetails currentUser;

    @BeforeEach
    void setUp() {
        currentUser = SecurityUser.fromUser(AuthorGenerator.generateAuthorWithId());
        authors = AuthorGenerator.generateAuthorsWithId(5);
    }

    @SneakyThrows
    @Test
    void getAllAuthorsTest(){
        var authorsDtos = mapToAuthorsDto(authors);
        Page<AuthorsDto> page = new PageImpl<>(authorsDtos);

        when(service.getAllAuthors(anyLong(),any(Pageable.class)))
                .thenReturn(page);

        mockMvc.perform(get("/api/authors")
                        .contentType(APPLICATION_JSON)
                        .with(user(currentUser)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].fullName", is(authorsDtos.get(0).getFullName())))
                .andExpect(jsonPath("$.content[1].fullName", is(authorsDtos.get(1).getFullName())));

    }

    @SneakyThrows
    @Test
    void getEmptyPageOfAuthorsTest(){
        when(service.getAllAuthors(anyLong(),any(Pageable.class)))
                .thenThrow(NotFoundException.class);

        mockMvc.perform(get("/api/authors")
                        .contentType(APPLICATION_JSON)
                        .with(user(currentUser)))
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
                .andExpect(jsonPath("$.fullName",is(authorDto.getFullName())))
                .andExpect(jsonPath("$.email",is(authorDto.getEmail())));
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
    void updateAuthorTest(){
        var author = authors.get(2);

        mockMvc.perform(put("/api/authors/"+author.getId())
                        .contentType(APPLICATION_JSON)
                        .content(mapObjectToString(author))
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().is(201))
                .andExpect(result -> assertThat(Objects.requireNonNull(result.getResponse().getRedirectedUrl())
                        .contains("http://localhost/api/authors/"))
                        .isTrue()
                );
    }

    @Test
    @SneakyThrows
    void updateAuthorWithFullNameWhichAlreadyExist(){
        var author = authors.get(2);
        var authorDto = new UpdateAuthorDto(author.getFullName(), author.getEmail(), author.getDescription(),author.getPassword());

        BDDMockito.willThrow(AuthorAlreadyExist.class).given(service).updateAuthor(author.getId(),authorDto);

        mockMvc.perform(put("/api/authors/"+author.getId())
                        .contentType(APPLICATION_JSON)
                        .content(mapObjectToString(authorDto))
                        .with(csrf()))
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

        mockMvc.perform(patch("/api/authors/"+author.getId()+"/password")
                        .contentType(APPLICATION_JSON)
                        .content(mapObjectToString(password))
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isNoContent())
                .andExpect(result -> assertThat(result.getResponse().getRedirectedUrl())
                        .isEqualTo("http://localhost/api/authors/"+author.getId()+"/password")
                );
    }

    @Test
    @SneakyThrows
    void updateAuthorPasswordWithWrongOldIdTest(){
        var author = authors.get(2);

        var password = new PasswordDto("old","new");

        BDDMockito.willThrow(IncorrectAuthorDetailsException.class)
                .given(service).updateAuthorPassword(author.getId(),password);

        mockMvc.perform(patch("/api/authors/"+author.getId()+"/password")
                        .contentType(APPLICATION_JSON)
                        .content(mapObjectToString(password))
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertThat(result.getResolvedException())
                        .isInstanceOf(IncorrectAuthorDetailsException.class)
                );
    }

    @Test
    @SneakyThrows
    void updateAuthorImageUrlTest(){
        MockMultipartFile file = new MockMultipartFile("file", "hello.txt", MediaType.TEXT_PLAIN_VALUE,
                "Hello, World!".getBytes()
        );

        mockMvc.perform(multipart("/api/authors/1/image")
                        .file(file)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    @SneakyThrows
    void updateAmountOfAuthorSubscriptionsTest(){

        mockMvc.perform(patch("/api/authors/1/subscriptions/2")
                        .contentType(APPLICATION_JSON)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isNoContent());
    }


    @Test
    @SneakyThrows
    void deleteAuthorTest(){
        var author = authors.get(0);

        when(service.deleteAuthor(author.getId()))
                .thenReturn(author);

        when(service.deleteAuthor(1L)).thenReturn(author);

        mockMvc.perform(delete("/api/authors/1")
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName",is(author.getFullName())))
                .andExpect(jsonPath("$.email",is(author.getEmail())));
    }


    @Test
    @SneakyThrows
    void getSubscriptionsTest(){
        var subscriptions = mapToAuthorsDto(List.of(authors.get(0)));

        when(service.getAuthorSubscriptions(anyLong(),any(Pageable.class)))
                .thenReturn(new PageImpl<>(subscriptions));

        mockMvc.perform(get("/api/authors/1/subscriptions")
                        .with(user(currentUser)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].fullName",is(subscriptions.get(0).getFullName())))
                .andExpect(jsonPath("$.content[0].imagePath",is(subscriptions.get(0).getImagePath())));
    }

    @Test
    @SneakyThrows
    void getSubscriptionsWhenTheyAreNotExistTest(){

        when(service.getAuthorSubscriptions(anyLong(),any(Pageable.class)))
                .thenThrow(NotFoundException.class);

        mockMvc.perform(get("/api/authors/1/subscriptions")
                        .with(user(currentUser)))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(r-> assertThat(r.getResolvedException())
                        .isInstanceOf(NotFoundException.class));
    }


    @Test
    @SneakyThrows
    void getSubscribersTest(){
        var subscriptions = mapToAuthorsDto(List.of(authors.get(0)));

        when(service.getAuthorSubscribers(anyLong(),any(Pageable.class)))
                .thenReturn(new PageImpl<>(subscriptions));

        mockMvc.perform(get("/api/authors/1/subscribers")
                        .with(user(currentUser)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].fullName",is(subscriptions.get(0).getFullName())))
                .andExpect(jsonPath("$.content[0].imagePath",is(subscriptions.get(0).getImagePath())));
    }

    @Test
    @SneakyThrows
    void getSubscribersWhenTheyAreNotExistDbTest(){
        when(service.getAuthorSubscribers(anyLong(),any(Pageable.class)))
                .thenThrow(NotFoundException.class);

        mockMvc.perform(get("/api/authors/1/subscribers")
                        .with(user(currentUser)))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(r-> assertThat(r.getResolvedException())
                        .isInstanceOf(NotFoundException.class));
    }


    @Test
    @SneakyThrows
    void getLikesTest(){
        var likes = List.of(new PoemsDto(1L,"name","text",
                1L, PUBLIC,"author",2L,1L));

        when(service.getAuthorLikes(anyLong(),any(Pageable.class)))
                .thenReturn(new PageImpl<>(likes));

        mockMvc.perform(get("/api/authors/1/likes")
                        .with(user(currentUser)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name",is(likes.get(0).getName())))
                .andExpect(jsonPath("$.content[0].text",is(likes.get(0).getText())));
    }

    @Test
    @SneakyThrows
    void getLikesWhenTheyAreNotExistTest(){

        when(service.getAuthorLikes(anyLong(),any(Pageable.class)))
                .thenThrow(NotFoundException.class);

        mockMvc.perform(get("/api/authors/1/likes")
                        .with(user(currentUser)))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(r-> assertThat(r.getResolvedException())
                        .isInstanceOf(NotFoundException.class));
    }



    @SneakyThrows
    private<T> String mapObjectToString(T author){
        var objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        return objectMapper.writeValueAsString(author);
    }

    private List<AuthorsDto> mapToAuthorsDto(List<Author>authors){
        return authors.stream().map(a -> new AuthorsDto(a.getId(),a.getFullName(),a.getDescription()
                        ,a.getImageName(), (long) a.getSubscribers().size(), (long) a.getPoems().size(),false))
                .toList();
    }
}
