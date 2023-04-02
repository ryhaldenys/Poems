package ua.poems_club.service;

import org.springframework.web.multipart.MultipartFile;
import ua.poems_club.dto.author.PasswordDto;
import ua.poems_club.dto.author.UpdateAuthorDto;
import ua.poems_club.model.Author;
import ua.poems_club.security.dto.RegistrationRequestDto;

public interface ManagementAuthorService {
    Author createAuthor(RegistrationRequestDto author);
    Author updateAuthor(Long id, UpdateAuthorDto author);
    void updateAuthorPassword(Long id, PasswordDto password);
    Author deleteAuthor(Long id);
    void addAuthorImage(Long id, MultipartFile imageUrl);
    void updateAuthorSubscriptions(Long authorId,Long subscriptionId);
    void deleteImage(Long id);
}
