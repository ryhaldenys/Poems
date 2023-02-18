package ua.poems_club.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ua.poems_club.dto.poem.PoemsDto;
import ua.poems_club.exception.NotFoundException;
import ua.poems_club.generator.AuthorGenerator;
import ua.poems_club.generator.PoemGenerator;
import ua.poems_club.model.Author;
import ua.poems_club.model.Poem;
import ua.poems_club.repository.AuthorRepository;
import ua.poems_club.repository.PoemRepository;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles(profiles = "test")
@Transactional
public class PoemServiceTest {
    @Autowired
    private PoemRepository poemRepository;
    @Autowired
    private PoemService poemService;
    @Autowired
    private AuthorRepository authorRepository;

    private List<Author> authors;
    private List<Poem> poems;
    private List<PoemsDto> poemsDtos;


    @BeforeEach
    void setUp() {
        addDataToDB();
        poemsDtos = mapToPoemsDto(poems);
    }

    @Test
    void getAllPoems(){
        var foundPoems = poemService.getAllPoems(any(Pageable.class),"").getContent();
        assertThat(foundPoems).isEqualTo(poemsDtos);
    }

    private List<PoemsDto> mapToPoemsDto(List<Poem>poems) {
        return poems.stream().map((p)-> new PoemsDto(p.getId(),p.getName(),p.getText(),p.getAuthor().getId(),
                        p.getAuthor().getFullName(),(long) p.getLikes().size(),false))
                .collect(Collectors.toList());
    }

    @Test
    void getAllPoemsByAuthorName(){
        var foundPoems = poemService.getAllPoems(PageRequest.of(0,poems.size()), poems.get(0).getName()).getContent();
        assertThat(foundPoems.get(0)).isEqualTo(mapToPoemsDto(poems).get(0));
    }

    @Test
    void getAllPoemsFromEmptyDb(){
        authorRepository.deleteAll();
        assertThatThrownBy(()->poemService.getAllPoems(any(Pageable.class),""))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void getPoemByIdFromEmptyDb(){
        assertThatThrownBy(()->poemService.getPoemById(1L))
                .isInstanceOf(NotFoundException.class);
    }


    private void addDataToDB(){
        authors = AuthorGenerator.generateAuthorsWithoutId(5);
        poems = PoemGenerator.generatePoemsWithoutId(5);

        for (int i = 0; i < authors.size(); i++) {
            poems.get(i).addAuthor(authors.get(i));
        }
        authorRepository.saveAll(authors);


        authors.get(0).addAllLikes(new HashSet<>(poems));
        authors.get(0).addSubscriber(authors.get(1));
        authors.get(0).addSubscriber(authors.get(2));
        authors.get(3).addSubscriber(authors.get(0));
    }
    @AfterEach
    void tearDown() {
        //poemRepository.deleteAll();
    }
}
