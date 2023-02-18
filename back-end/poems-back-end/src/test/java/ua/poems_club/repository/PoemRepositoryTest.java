package ua.poems_club.repository;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import ua.poems_club.dto.poem.PoemsDto;
import ua.poems_club.generator.AuthorGenerator;
import ua.poems_club.generator.PoemGenerator;
import ua.poems_club.model.Author;
import ua.poems_club.model.Poem;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@DataJpaTest
public class PoemRepositoryTest {
    @Autowired
    private PoemRepository poemRepository;

    private List<Author> authors;
    private List<Poem> poems;
    @Autowired
    private AuthorRepository authorRepository;

    @BeforeEach
    void setUp() {
        addDataToDB();
    }

    @Test
    void findAllPoemsTest(){
        var poemsDtos = mapToPoemsDto();
        var foundPoems = poemRepository.findAllPoems(any(Pageable.class));

        foundPoems.forEach(p-> System.out.println(p.amountLikes()));

        assertThat(foundPoems.getContent()).isEqualTo(poemsDtos);
    }

    private List<PoemsDto> mapToPoemsDto(){
        return poems.stream().map((p)-> new PoemsDto(p.getId(),p.getName(),p.getText(),p.getAuthor().getId(),
                        p.getAuthor().getFullName(),(long) p.getLikes().size(),false))
                .collect(Collectors.toList());
    }

    @Test
    void findAllPoemsByNameTest(){
        var poemsDtos = mapToPoemsDto();

        var foundPoems = poemRepository.findAllPoemsByName(any(Pageable.class),poemsDtos.get(0).name())
                .getContent();

        assertThat(foundPoems.get(0)).isEqualTo(poemsDtos.get(0));
    }

    @Test
    void findByIdTest(){
        var poem = poems.get(0);
        var foundPoem = poemRepository.findPoemById(poem.getId()).orElseThrow();

        assertThat(foundPoem.id()).isEqualTo(poem.getId());
        assertThat(foundPoem.name()).isEqualTo(poem.getName());
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
