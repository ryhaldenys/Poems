package ua.poems_club.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ua.poems_club.dto.poem.RequestPoemDto;
import ua.poems_club.exception.NotFoundException;

import ua.poems_club.model.Author;
import ua.poems_club.model.Poem;

import ua.poems_club.repository.AuthorRepository;
import ua.poems_club.repository.PoemRepository;

import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static ua.poems_club.generator.AuthorGenerator.*;
import static ua.poems_club.generator.PoemGenerator.*;
import static ua.poems_club.model.Poem.Status.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class AuthorPoemServiceTest {
    @Autowired
    private AuthorPoemService authorPoemService;

    @Autowired
    private AuthorRepository authorRepository;

    private List<Author> authors;
    private Author currentUser;
    @Autowired
    private PoemRepository poemRepository;

    @BeforeEach
    void setUp() {
        addDataToDB();
    }

    @Test
    void getAllPublicPoemsByAuthorIdTest(){
        var author = authors.get(0);
        var poemName = author.getPoems().get(0).getName();
        var foundPoems = authorPoemService.getAllPublicPoemsByAuthorIdAndContainText(author.getId(), currentUser.getId(), poemName, PageRequest.of(0,10)).getContent();

        assertThat(foundPoems.get(0).getId()).isEqualTo(author.getPoems().get(0).getId());
        assertThat(foundPoems.get(0).getAmountLikes()).isEqualTo(1L);
        assertThat(foundPoems.get(0).getStatus()).isEqualTo(PUBLIC);
    }

    @Test
    void getAllPublicPoemsByWrongAuthorIdTest(){

        assertThatThrownBy(()->authorPoemService.getAllPublicPoemsByAuthorIdAndContainText(1000L, currentUser.getId(), " ",PageRequest.of(0,10)))
                .isInstanceOf(NotFoundException.class);
    }


    @Test
    void getAllPoemsByAuthorIdTest(){
        var author = authors.get(1);
        var foundPoems = authorPoemService.getAllPoemsByAuthorId(author.getId(),currentUser.getId(), PageRequest.of(0,10)).getContent();

        assertThat(foundPoems.get(0).getId()).isEqualTo(author.getPoems().get(0).getId());
        assertThat(foundPoems.get(0).isLike()).isTrue();
        assertThat(foundPoems.get(0).getStatus()).isEqualTo(PRIVATE);
    }

    @Test
    void getAllPoemsByWrongAuthorIdTest(){
        assertThatThrownBy(()->authorPoemService.getAllPoemsByAuthorId(1000L, currentUser.getId(), PageRequest.of(0,10)))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void savePoemTest(){
        var author = authors.get(1);
        var requestBody = new RequestPoemDto("new poem","text",null);

        authorPoemService.createPoem(author.getId(),requestBody);
        poemRepository.flush();

        var foundPoems = poemRepository.findAll();
        assertThat(checkIsPoem(foundPoems,author.getId(),requestBody)).isTrue();
    }

    private boolean checkIsPoem(List<Poem> poems,Long authorId, RequestPoemDto requestBody){
        return poems.stream()
                .anyMatch(p -> p.getName().equals(requestBody.name())
                        && p.getAuthor().getId().equals(authorId)
                        && p.getStatus().equals(PRIVATE));
    }

    @Test
    void updatePoemTest(){
        var author = authors.get(1);
        var poem = author.getPoems().get(0);
        var requestBody = new RequestPoemDto("updated name","updated text",null);

        authorPoemService.updatePoem(author.getId(),poem.getId(),requestBody);
        poemRepository.flush();

        var foundPoems = poemRepository.findAll();
        assertThat(checkIsPoem(foundPoems,author.getId(),requestBody)).isTrue();
    }

    @Test
    void deletePoemTest(){
        var author = authors.get(3);
        var poem = author.getPoems().get(0);
        authorPoemService.deletePoem(author.getId(),poem.getId());
        poemRepository.flush();

        var foundPoems = poemRepository.findById(poem.getId())
                .orElseThrow();
        assertThat(foundPoems).isNotNull();
    }


    @Test
    void updateLikesTest(){
        var author = authors.get(0);
        var poem = author.getPoems().get(0);
        authorPoemService.updatePoemLikes(author.getId(),poem.getId());
        poemRepository.flush();

        var foundPoem = poemRepository.findById(poem.getId())
                .orElseThrow();
        assertThat(foundPoem.getLikes().contains(author)).isFalse();
    }



    private void addDataToDB(){
        authors = generateAuthorsWithoutId(5);
        List<Poem> poems = generatePoemsWithoutId(5);
        poems.get(1).setStatus(PRIVATE);

        for (int i = 0; i < authors.size(); i++) {
            poems.get(i).addAuthor(authors.get(i));
        }
        authorRepository.saveAll(authors);

        authors.get(0).addAllLikes(new HashSet<>(poems));

        authors.get(0).addSubscriber(authors.get(1));
        authors.get(0).addSubscriber(authors.get(2));
        authors.get(3).addSubscriber(authors.get(0));

        currentUser = generateAuthorWithoutId();
        authorRepository.save(currentUser);
        currentUser.addLike(poems.get(1));
        poemRepository.flush();
    }

}


