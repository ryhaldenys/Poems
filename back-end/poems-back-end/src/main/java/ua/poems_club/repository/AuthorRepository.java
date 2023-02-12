package ua.poems_club.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ua.poems_club.dto.AuthorDto;
import ua.poems_club.dto.AuthorsDto;
import ua.poems_club.model.Author;

import java.util.Optional;

public interface AuthorRepository extends JpaRepository<Author,Long> {
    @Query("select new ua.poems_club.dto.AuthorsDto(a.id,a.fullName,a.description,a.imageUrl, count(s),count(p))" +
            " from Author a left join a.subscribers s left join a.poems p group by a")
    Page<AuthorsDto> findAllAuthors(Pageable pageable);

    @Query("select new ua.poems_club.dto.AuthorDto(a.id,a.fullName,a.description,a.email,a.imageUrl," +
            "count(p),count(subscribe),count(subscrip),count(l))" +
            " from Author a left join a.subscribers subscribe" +
            " left join a.subscriptions subscrip left join a.myLikes l" +
            " left join a.poems p where a.id=?1 group by a")
    Optional<AuthorDto> findAuthorById(Long id);

    Optional<Author> findAuthorByEmail(String email);
    Optional<Author> findAuthorByFullName(String fullName);

}
