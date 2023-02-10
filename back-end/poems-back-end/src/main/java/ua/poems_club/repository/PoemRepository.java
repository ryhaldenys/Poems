package ua.poems_club.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.poems_club.model.Poem;

public interface PoemRepository extends JpaRepository<Poem,Long> {
}
