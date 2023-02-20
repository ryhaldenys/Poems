package ua.poems_club.repository;

import jakarta.persistence.QueryHint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import ua.poems_club.dto.author.AuthorDto;
import ua.poems_club.dto.author.AuthorsDto;
import ua.poems_club.model.Author;

import java.util.Optional;

public interface AuthorRepository extends JpaRepository<Author,Long> {
    @QueryHints(
            @QueryHint(name ="org.hibernate.jpa.QueryHints.HINT_PASS_DISTINCT_THROUGH",value = "false"))
    @Query("select distinct new ua.poems_club.dto.author.AuthorsDto(a.id,a.fullName,a.description,a.imageUrl, count(s),count(p),false)" +
            " from Author a left join a.subscribers s left join a.poems p group by a")
    Page<AuthorsDto> findAllAuthors(Pageable pageable);
    //todo: fix bag


    @QueryHints(
            @QueryHint(name ="org.hibernate.jpa.QueryHints.HINT_PASS_DISTINCT_THROUGH",value = "false"))
    @Query("select distinct new ua.poems_club.dto.author.AuthorDto(a.id,a.fullName,a.description,a.email,a.imageUrl," +
            "count(p),count(subscribe),count(subscrip),count(l))" +
            " from Author a left join a.subscribers subscribe" +
            " left join a.subscriptions subscrip left join a.myLikes l" +
            " left join a.poems p where a.id=?1 group by a")
    Optional<AuthorDto> findAuthorById(Long id);

    Optional<Author> findByEmail(String email);
    Optional<Author> findByFullName(String fullName);


    @QueryHints(
            @QueryHint(name ="org.hibernate.jpa.QueryHints.HINT_PASS_DISTINCT_THROUGH",value = "false"))
    @Query("select distinct a from Author a left join fetch a.subscriptions where a.id =?1")
    Optional<Author> findAuthorFetchSubscriptions(Long id);

    @QueryHints(
            @QueryHint(name ="org.hibernate.jpa.QueryHints.HINT_PASS_DISTINCT_THROUGH",value = "false"))
    @Query("select distinct a from Author a left join fetch a.subscribers where a.id =?1")
    Optional<Author> findAuthorFetchSubscribers(Long id);
}
