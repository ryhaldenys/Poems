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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import ua.poems_club.dto.poem.PoemDto;
import ua.poems_club.dto.poem.PoemsDto;
import ua.poems_club.exception.NotFoundException;
import ua.poems_club.model.Author;
import ua.poems_club.model.Poem;
import ua.poems_club.security.model.JwtTokenProvider;
import ua.poems_club.security.model.SecurityUser;
import ua.poems_club.service.PoemService;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;
import static org.hamcrest.core.Is.*;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;

import static ua.poems_club.generator.AuthorGenerator.*;
import static ua.poems_club.generator.PoemGenerator.*;


@WithMockUser
@WebMvcTest(PoemController.class)
public class PoemControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PoemService service;

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
    void getAllTest(){
        when(service.getAllPoems(anyLong(), any(Pageable.class),anyString()))
                .thenReturn(new PageImpl<>(mapToPoemsDto(poems)));

        mockMvc.perform(get("/api/poems")
                .contentType(APPLICATION_JSON)
                        .with(user(securityUser)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name", is(poems.get(0).getName())))
                .andExpect(jsonPath("$.content[1].name", is(poems.get(1).getName())));
    }

    @Test
    @SneakyThrows
    void getAllFromEmptyDBTest(){
        when(service.getAllPoems(anyLong(), any(Pageable.class),anyString()))
                .thenThrow(NotFoundException.class);

        mockMvc.perform(get("/api/poems")
                        .contentType(APPLICATION_JSON)
                        .with(user(securityUser)))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(result -> assertThat(result.getResolvedException())
                        .isInstanceOf(NotFoundException.class)
                );
    }

    @Test
    @SneakyThrows
    void getByIdTest(){
        var poem = poems.get(0);
        when(service.getPoemById(anyLong()))
                .thenReturn(mapToPoemDto(poem));

        mockMvc.perform(get("/api/poems/1")
                        .contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(poem.getName())))
                .andExpect(jsonPath("$.text", is(poem.getText())));
    }

    @Test
    @SneakyThrows
    void getPoemByIdFromEmptyDBTest(){
        when(service.getPoemById(1L))
                .thenThrow(NotFoundException.class);

        mockMvc.perform(get("/api/poems/1")
                        .contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(result -> assertThat(result.getResolvedException())
                        .isInstanceOf(NotFoundException.class)
                );
    }


    private List<PoemsDto> mapToPoemsDto(List<Poem>poems) {
        return poems.stream().map((p)-> new PoemsDto(p.getId(),p.getName(),p.getText(),p.getAuthor().getId(),
                        p.getStatus(),p.getAuthor().getFullName(),(long) p.getLikes().size(),false))
                .collect(Collectors.toList());
    }

    private PoemDto mapToPoemDto(Poem poem) {
        return new PoemDto(poem.getId(), poem.getName(), poem.getText(),poem.getStatus(), poem.getAuthor().getId(),
                poem.getAuthor().getFullName());
    }

}
