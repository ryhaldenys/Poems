package ua.poems_club.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ua.poems_club.dto.AuthorDto;
import ua.poems_club.dto.AuthorsDto;

public interface AuthorService {
    Page<AuthorsDto> getAllAuthors(Pageable pageable);
    AuthorDto getAuthorById(Long id);
}
