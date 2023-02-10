package ua.poems_club.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.poems_club.model.Author;

public interface AuthorRepository extends JpaRepository<Author,Long> {
}
