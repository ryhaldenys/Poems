package ua.poems_club.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import ua.poems_club.dto.poem.PoemsDto;
import ua.poems_club.dto.poem.RequestPoemDto;
import ua.poems_club.exception.NotFoundException;
import ua.poems_club.model.Author;
import ua.poems_club.model.Poem;
import ua.poems_club.security.model.JwtTokenProvider;
import ua.poems_club.security.model.SecurityUser;
import ua.poems_club.service.AuthorPoemService;


import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ua.poems_club.generator.AuthorGenerator.generateAuthorWithId;
import static ua.poems_club.generator.AuthorGenerator.generateAuthorsWithId;
import static ua.poems_club.generator.PoemGenerator.generatePoemsWithId;

@WebMvcTest(AuthorPoemsController.class)
public class AuthorPoemControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthorPoemService service;

    @MockBean
    private JwtTokenProvider tokenProvider;

    private List<Poem> poems;
    private UserDetails securityUser;

    @BeforeEach
    void setUp() {
        securityUser = SecurityUser.fromUser(generateAuthorWithId());
        List<Author> authors = generateAuthorsWithId(5);
        poems = generatePoemsWithId(5);
        for (int i = 0; i < authors.size(); i++) {
            authors.get(i).addPoem(poems.get(i));
        }
        Author author = authors.get(0);
    }

    @Test
    @SneakyThrows
    void getAllPoemsOfAuthorTest(){
        when(service.getAllPublicPoemsByAuthorId(anyLong(), anyLong(),any(Pageable.class)))
                .thenReturn(new PageImpl<>(mapToPoemsDto(poems)));

        mockMvc.perform(get("/api/authors/1/poems")
                        .contentType(APPLICATION_JSON)
                        .with(user(securityUser)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name", is(poems.get(0).getName())))
                .andExpect(jsonPath("$.content[1].name", is(poems.get(1).getName())));
    }

    @Test
    @SneakyThrows
    void getEmptyPagePoemsOfAuthorTest(){
        when(service.getAllPublicPoemsByAuthorId(anyLong(), anyLong(),any(Pageable.class)))
                .thenThrow(NotFoundException.class);

        mockMvc.perform(get("/api/authors/1/poems")
                        .contentType(APPLICATION_JSON)
                        .with(user(securityUser)))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(result -> Assertions.assertThat(result.getResolvedException())
                        .isInstanceOf(NotFoundException.class));
    }

    @Test
    @SneakyThrows
    void getAllPoemsOfCurrentAuthorTest(){
        when(service.getAllPoemsByAuthorId(anyLong(), anyLong(),any(Pageable.class)))
                .thenReturn(new PageImpl<>(mapToPoemsDto(poems)));

        mockMvc.perform(get("/api/authors/1/poems/own")
                        .contentType(APPLICATION_JSON)
                        .with(user(securityUser)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name", is(poems.get(0).getName())))
                .andExpect(jsonPath("$.content[1].name", is(poems.get(1).getName())));
    }

    @Test
    @SneakyThrows
    void getEmptyPagePoemsOfCurrentAuthorTest(){
        when(service.getAllPoemsByAuthorId(anyLong(), anyLong(),any(Pageable.class)))
                .thenThrow(NotFoundException.class);

        mockMvc.perform(get("/api/authors/1/poems/own")
                        .contentType(APPLICATION_JSON)
                        .with(user(securityUser)))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(result -> Assertions.assertThat(result.getResolvedException())
                        .isInstanceOf(NotFoundException.class));
    }

    @Test
    @SneakyThrows
    void createPoemTest(){
        var requestBody = new RequestPoemDto("name","text","PUBLIC");

        mockMvc.perform(post("/api/authors/1/poems")
                        .contentType(APPLICATION_JSON)
                        .content(mapObjectToString(requestBody))
                        .with(user(securityUser))
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    @SneakyThrows
    void createPoemByWrongAuthorIdTest(){
        var requestBody = new RequestPoemDto("name","text","PUBLIC");

        BDDMockito.willThrow(NotFoundException.class).given(service)
                        .createPoem(anyLong(),any(RequestPoemDto.class));

        mockMvc.perform(post("/api/authors/1/poems")
                        .contentType(APPLICATION_JSON)
                        .content(mapObjectToString(requestBody))
                        .with(csrf())
                        .with(user(securityUser)))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    void updatePoemTest(){
        var requestBody = new RequestPoemDto("name","text","PUBLIC");
        mockMvc.perform(put("/api/authors/1/poems/1")
                        .contentType(APPLICATION_JSON)
                        .content(mapObjectToString(requestBody))
                        .with(csrf())
                        .with(user(securityUser)))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    @SneakyThrows
    void updatePoemByWrongIdsTest(){
        var requestBody = new RequestPoemDto("name","text","PUBLIC");

        BDDMockito.willThrow(NotFoundException.class).given(service)
                .updatePoem(anyLong(),anyLong(),any(RequestPoemDto.class));

        mockMvc.perform(put("/api/authors/1/poems/1")
                        .contentType(APPLICATION_JSON)
                        .content(mapObjectToString(requestBody))
                        .with(csrf())
                        .with(user(securityUser)))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    void deletePoemTest(){
        mockMvc.perform(delete("/api/authors/1/poems/1")
                        .contentType(APPLICATION_JSON)
                        .with(csrf())
                        .with(user(securityUser)))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    @SneakyThrows
    void deletePoemByWrongIdTest(){
        BDDMockito.willThrow(NotFoundException.class).given(service)
                .deletePoem(anyLong(),anyLong());

        mockMvc.perform(delete("/api/authors/1/poems/1")
                        .contentType(APPLICATION_JSON)
                        .with(csrf())
                        .with(user(securityUser)))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    void updateLikes(){
        mockMvc.perform(patch("/api/authors/1/poems/1/likes")
                        .contentType(APPLICATION_JSON)
                        .with(csrf())
                        .with(user(securityUser)))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    @SneakyThrows
    void updateLikesByWrongIds(){

        BDDMockito.willThrow(NotFoundException.class).given(service)
                        .updatePoemLikes(anyLong(),anyLong());

        mockMvc.perform(patch("/api/authors/1/poems/1/likes")
                        .contentType(APPLICATION_JSON)
                        .with(csrf())
                        .with(user(securityUser)))
                .andDo(print())
                .andExpect(status().isNotFound());
    }



    private List<PoemsDto> mapToPoemsDto(List<Poem>poems) {
        return poems.stream().map((p)-> new PoemsDto(p.getId(),p.getName(),p.getText(),p.getAuthor().getId(),
                        p.getStatus(),p.getAuthor().getFullName(),(long) p.getLikes().size(),false))
                .collect(Collectors.toList());
    }

    @SneakyThrows
    private<T> String mapObjectToString(T object){
        return new ObjectMapper().writeValueAsString(object);
    }
}
