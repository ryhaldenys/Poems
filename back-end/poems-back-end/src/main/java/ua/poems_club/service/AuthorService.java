package ua.poems_club.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ua.poems_club.dto.*;
import ua.poems_club.model.Author;

public interface AuthorService {
    Page<AuthorsDto> getAllAuthors(Pageable pageable);
    AuthorDto getAuthorById(Long id);
    Long createAuthor(CreateAuthorDto author);
    void updateAuthor(Long id, UpdateAuthorDto author);
    void updateAuthorPassword(Long id, PasswordDto password);
}
