package ua.poems_club.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ua.poems_club.dto.AuthorsDto;
import ua.poems_club.model.Author;

public interface AuthorRepository extends JpaRepository<Author,Long> {
    @Query("select new ua.poems_club.dto.AuthorsDto(a.id,a.fullName,a.description, count(s),count(p))" +
            " from Author a left join a.subscribers s left join a.poems p group by a")
    Page<AuthorsDto> findAllAuthors(Pageable pageable);
}
