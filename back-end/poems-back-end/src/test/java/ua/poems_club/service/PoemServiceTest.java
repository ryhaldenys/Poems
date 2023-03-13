package ua.poems_club.service;

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

@SpringBootTest
@ActiveProfiles(profiles = "test")
@Transactional
public class PoemServiceTest {
    @Autowired
    private PoemService poemService;
    @Autowired
    private AuthorRepository authorRepository;

    private List<Author> authors;
    private List<Poem> poems;
    private Author currentUser;


    @BeforeEach
    void setUp() {
        addDataToDB();
        currentUser = authors.get(0);
    }

    @Test
    void getAllPoems(){
        var foundPoems = poemService.getAllPoems(currentUser.getId(), Pageable.unpaged(),"").getContent();
        List<PoemsDto> poemsDtos = mapToPoemsDto(currentUser, poems);

        assertThat(foundPoems).isEqualTo(poemsDtos);
    }

    private List<PoemsDto> mapToPoemsDto(Author currentUser,List<Poem>poems) {
        return poems.stream().map((p)-> new PoemsDto(p.getId(),p.getName(),p.getText(),p.getAuthor().getId(),
                        p.getStatus(),p.getAuthor().getFullName(),(long) p.getLikes().size(),p.getLikes().contains(currentUser)))
                .collect(Collectors.toList());
    }

    @Test
    void getAllPoemsByAuthorName(){
        var foundPoems = poemService.getAllPoems(currentUser.getId(), PageRequest.of(0,poems.size()), poems.get(0).getName()).getContent();
        assertThat(foundPoems.get(0)).isEqualTo(mapToPoemsDto(currentUser,poems).get(0));
    }

    @Test
    void getAllPoemsFromEmptyDb(){
        authorRepository.deleteAll();
        assertThatThrownBy(()->poemService.getAllPoems(currentUser.getId(), Pageable.unpaged(),""))
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

}
