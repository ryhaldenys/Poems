package ua.poems_club.controller;

import lombok.SneakyThrows;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import ua.poems_club.dto.AuthorsDto;

import ua.poems_club.exception.NotFoundException;

import ua.poems_club.service.AuthorService;

import java.util.List;


import static org.assertj.core.api.Assertions.*;
import static org.hamcrest.core.Is.*;
import static org.mockito.Mockito.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthorController.class)
public class AuthorControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthorService service;


    @SneakyThrows
    @Test
    void getAllAuthorsTest(){

        List<AuthorsDto> authors = List.of(
                new AuthorsDto(1L,"fullName1","description1",12L,12L),
                new AuthorsDto(2L,"fullName2","description2",22L,22L)
        );

        Page<AuthorsDto> page = new PageImpl<>(authors);

        when(service.getAllAuthors(any(Pageable.class)))
                .thenReturn(page);

        mockMvc.perform(get("/api/authors")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].fullName", is("fullName1")))
                .andExpect(jsonPath("$.content[1].fullName", is("fullName2")));

    }


    @SneakyThrows
    @Test
    void getEmptyPageOfAuthorsTest(){

        when(service.getAllAuthors(any(Pageable.class)))
                .thenThrow(NotFoundException.class);

        mockMvc.perform(get("/api/authors")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(result -> assertThat(result.getResolvedException())
                        .isInstanceOf(NotFoundException.class));

    }
}
