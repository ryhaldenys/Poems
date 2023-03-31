package ua.poems_club.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ua.poems_club.dto.author.*;
import ua.poems_club.dto.poem.PoemsDto;
import ua.poems_club.model.Author;

public interface GettingDataAuthorService {
    Page<AuthorsDto> getAllAuthors(Long id,String authorName,Pageable pageable);
    Page<AuthorsDto> getAuthorsSortedBySubscribers(Long id, Pageable pageable);

    AuthorDto getAuthorById(Long id);
    Author getAuthorByEmail(String email);
    Page<AuthorsDto> getAuthorSubscriptions(Long id,String authorName,Pageable pageable);
    Page<AuthorsDto> getAuthorSubscribers(Long id,String authorName,Pageable pageable);
    Page<PoemsDto> getAuthorLikes(Long id,String poemName,Pageable pageable);
}