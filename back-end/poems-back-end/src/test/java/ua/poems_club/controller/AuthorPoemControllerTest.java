package ua.poems_club.controller;

import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import ua.poems_club.dto.poem.PoemsDto;
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
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

    private List<Author> authors;
    private List<Poem> poems;
    private Author author;
    private UserDetails securityUser;

    @BeforeEach
    void setUp() {
        securityUser = SecurityUser.fromUser(generateAuthorWithId());
        authors = generateAuthorsWithId(5);
        poems = generatePoemsWithId(5);
        for (int i = 0; i < authors.size(); i++) {
            authors.get(i).addPoem(poems.get(i));
        }
        author = authors.get(0);
    }

    @Test
    @SneakyThrows
    void getAllPoemsOfAuthor(){
        when(service.getAllByAuthorId(anyLong(), anyLong(),any(Pageable.class)))
                .thenReturn(new PageImpl<>(mapToPoemsDto(poems)));

        mockMvc.perform(get("/api/authors/1/poems")
                        .contentType(APPLICATION_JSON)
                        .with(user(securityUser)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name", is(poems.get(0).getName())))
                .andExpect(jsonPath("$.content[1].name", is(poems.get(1).getName())));
    }


    private List<PoemsDto> mapToPoemsDto(List<Poem>poems) {
        return poems.stream().map((p)-> new PoemsDto(p.getId(),p.getName(),p.getText(),p.getAuthor().getId(),
                        p.getAuthor().getFullName(),(long) p.getLikes().size(),false))
                .collect(Collectors.toList());
    }
}
