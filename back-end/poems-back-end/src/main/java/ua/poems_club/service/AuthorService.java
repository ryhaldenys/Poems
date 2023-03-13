package ua.poems_club.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import ua.poems_club.dto.author.*;
import ua.poems_club.dto.poem.PoemsDto;
import ua.poems_club.model.Author;
import ua.poems_club.security.dto.RegistrationRequestDto;

public interface AuthorService {
    Page<AuthorsDto> getAllAuthors(Long id,Pageable pageable);
    AuthorDto getAuthorById(Long id);
    Author createAuthor(RegistrationRequestDto author);
    Author updateAuthor(Long id, UpdateAuthorDto author);
    void updateAuthorPassword(Long id, PasswordDto password);
    Author deleteAuthor(Long id);
    Author getAuthorByEmail(String email);
    void addAuthorImage(Long id, MultipartFile imageUrl);
    void updateAuthorSubscriptions(Long authorId,Long subscriptionId);

    void deleteImage(Long id);
    Page<AuthorsDto> getAuthorSubscriptions(Long id,Pageable pageable);
    Page<AuthorsDto> getAuthorSubscribers(Long id,Pageable pageable);
    Page<PoemsDto> getAuthorLikes(Long id,Pageable pageable);
}