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
import static ua.poems_club.model.Poem.Status.*;

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
        var currentUser = authors.get(1);
        var poemsDtos = mapToPoemsDto(currentUser);
        var foundPoems = poemRepository.findAllPoems(currentUser.getId(),Pageable.unpaged());

        assertThat(foundPoems.getContent()).isEqualTo(poemsDtos);
    }

    private List<PoemsDto> mapToPoemsDto(){
        return poems.stream().map((p)-> new PoemsDto(p.getId(),p.getName(),p.getText(),p.getAuthor().getId(),
                        p.getStatus(),p.getAuthor().getFullName(),(long) p.getLikes().size(),false))
                .collect(Collectors.toList());
    }

    private List<PoemsDto> mapToPoemsDto(Author author){
        return poems.stream()
                .filter(p-> p.getStatus().equals(PUBLIC))
                .map((p)-> new PoemsDto(p.getId(),p.getName(),p.getText(),p.getAuthor().getId(),
                        p.getStatus(),p.getAuthor().getFullName(),(long) p.getLikes().size(),p.getLikes().contains(author)))
                .collect(Collectors.toList());
    }

    @Test
    void findAllPoemsByNameTest(){
        var currentUser = authors.get(4);
        var poemsDtos = mapToPoemsDto(currentUser);

        var foundPoems = poemRepository.findAllPoemsByName(currentUser.getId(),Pageable.unpaged(),poemsDtos.get(2).getName())
                .getContent();
        System.out.println(foundPoems);

        assertThat(foundPoems.get(0)).isEqualTo(poemsDtos.get(0));
    }

    @Test
    void findByIdTest(){
        var poem = poems.get(0);
        var foundPoem = poemRepository.findPoemById(poem.getId()).orElseThrow();

        assertThat(foundPoem.id()).isEqualTo(poem.getId());
        assertThat(foundPoem.name()).isEqualTo(poem.getName());
    }


    @Test
    void findAllPublicPoemsByAuthorIdTest(){
        var author = authors.get(0);
        var currentAuthor = authors.get(1);
        var foundPoems = poemRepository.findAllPublicPoemsByAuthorId(author.getId(),currentAuthor.getId(),Pageable.unpaged())
                .getContent();

        assertThat(foundPoems.isEmpty()).isTrue();
    }

    @Test
    void findAllPoemsByAuthorIdTest(){
        var author = authors.get(1);
        var currentUser = authors.get(0);
        var poems = poemRepository.findAllPoemsByAuthorId(author.getId(),currentUser.getId(),Pageable.unpaged())
                .getContent();
        var poemsDtos = mapToPoemsDto();
        assertThat(poems.get(0)).isEqualTo(poemsDtos.get(0));
    }

    @Test
    void findPoemByAuthorIdAndIdTest(){
        var author = authors.get(0);
        var poem = author.getPoems().get(0);

        var foundPoem = poemRepository.findPoemByAuthorIdAndId(author.getId(),poem.getId())
                .orElseThrow();

        assertThat(foundPoem.getId()).isEqualTo(poem.getId());
        assertThat(foundPoem.getName()).isEqualTo(poem.getName());
        assertThat(foundPoem.getText()).isEqualTo(poem.getText());
    }

    @Test
    void findPoemByAuthorIdAndIdFetchLikesTest(){
        var author = authors.get(0);
        var poem = author.getPoems().get(0);

        var foundPoem = poemRepository.findPoemByAuthorIdAndIdFetchLikes(author.getId(),poem.getId())
                .orElseThrow();

        assertThat(foundPoem.getId()).isEqualTo(poem.getId());
        assertThat(foundPoem.getName()).isEqualTo(poem.getName());
        assertThat(foundPoem.getLikes()).isEqualTo(poem.getLikes());
    }

    @Test
    void findPoemByIdFetchLikesTest(){
        var poem = poems.get(0);
        var foundPoem = poemRepository.findPoemByIdFetchLikes(poem.getId())
                .orElseThrow();

        assertThat(foundPoem.getId()).isEqualTo(poem.getId());
        assertThat(foundPoem.getName()).isEqualTo(poem.getName());
        assertThat(foundPoem.getLikes()).isEqualTo(poem.getLikes());
    }


    @Test
    void findAllAuthorsLikesTest(){
        var currentUser = authors.get(0);
        var foundLikes = poemRepository.findAllAuthorLikes(currentUser.getId(),Pageable.unpaged())
                .getContent();

        assertThat(foundLikes).isEqualTo(mapToPoemsDto(currentUser));
    }


    private void addDataToDB(){
        authors = AuthorGenerator.generateAuthorsWithoutId(5);
        poems = PoemGenerator.generatePoemsWithoutId(5);
        poems.get(0).setStatus(PRIVATE);
        poems.get(1).setStatus(PRIVATE);

        for (int i = 0; i < authors.size(); i++) {
            poems.get(i).addAuthor(authors.get(i));
        }
        authorRepository.saveAll(authors);

        authors.get(1).addLike(poems.get(0));
        authors.get(0).addAllLikes(new HashSet<>(poems));
        authors.get(0).addSubscriber(authors.get(1));
        authors.get(1).addSubscriber(authors.get(0));
        authors.get(3).addSubscriber(authors.get(0));
    }
}
