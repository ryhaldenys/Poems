package ua.poems_club.repository;

import ua.poems_club.model.Author;

import java.util.Optional;

public interface CustomAuthorRepository {
    Optional<Author> findAuthorByIdFetchAllFields(Long id);
}
