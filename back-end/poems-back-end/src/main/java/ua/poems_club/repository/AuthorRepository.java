package ua.poems_club.repository;

import jakarta.persistence.QueryHint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;

import org.springframework.data.repository.query.Param;
import ua.poems_club.dto.author.AuthorDto;
import ua.poems_club.dto.author.AuthorsDto;
import ua.poems_club.model.Author;

import java.util.Optional;

public interface AuthorRepository extends JpaRepository<Author,Long>,CustomAuthorRepository {
    @QueryHints(
            @QueryHint(name ="org.hibernate.jpa.QueryHints.HINT_PASS_DISTINCT_THROUGH",value = "false"))
    @Query("select distinct new ua.poems_club.dto.author.AuthorsDto(a.id,a.fullName,a.description,a.imageName, count(distinct s),count(distinct p)," +
            "sum(case when s.id =:id then 1 else 0 end)" +
            ") " +
            " from Author a left join a.subscribers s left join a.poems p group by a")
    Page<AuthorsDto> findAllAuthors(@Param("id") Long id,Pageable pageable);


    @Query("select distinct new ua.poems_club.dto.author.AuthorsDto(a.id,a.fullName,a.description,a.imageName, count(distinct s),count(distinct p)," +
            "sum(case when s.id =:id then 1 else 0 end)" +
            ") " +
            " from Author a left join a.subscribers s left join a.poems p group by a " +
            "order by count (distinct s) desc ")
    Page<AuthorsDto> findAuthorsSortedBySubscribers(@Param("id") Long id,Pageable pageable);

    @QueryHints(
            @QueryHint(name ="org.hibernate.jpa.QueryHints.HINT_PASS_DISTINCT_THROUGH",value = "false"))
    @Query("select distinct new ua.poems_club.dto.author.AuthorsDto(a.id,a.fullName,a.description,a.imageName, count(distinct s),count(distinct p)," +
            "sum(case when s.id =:id then 1 else 0 end)) " +
            "from Author a left join a.subscribers s left join a.poems p " +
            "where lower(a.fullName) like lower(CONCAT('%',:name,'%'))"+
            "group by a")
    Page<AuthorsDto> findAllAuthorsByAuthorName(@Param("id") Long id,@Param("name") String name, Pageable pageable);


    @QueryHints(
            @QueryHint(name ="org.hibernate.jpa.QueryHints.HINT_PASS_DISTINCT_THROUGH",value = "false"))
    @Query("select distinct new ua.poems_club.dto.author.AuthorDto(a.id,a.fullName,a.description,a.email,a.imageName," +
            "count(distinct p),count(distinct subscribe),count(distinct subscrip),count(distinct l))" +
            " from Author a left join a.subscribers subscribe" +
            " left join a.subscriptions subscrip left join a.myLikes l" +
            " left join a.poems p where a.id=:id group by a")
    Optional<AuthorDto> findAuthorById(@Param("id") Long id);

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

    @QueryHints(
            @QueryHint(name ="org.hibernate.jpa.QueryHints.HINT_PASS_DISTINCT_THROUGH",value = "false"))
    @Query("select distinct a from Author a left join fetch a.myLikes where a.id =?1")
    Optional<Author> findByIdFetchLikes(Long likeId);

    @QueryHints(
            @QueryHint(name ="org.hibernate.jpa.QueryHints.HINT_PASS_DISTINCT_THROUGH",value = "false"))
    @Query("select distinct new ua.poems_club.dto.author.AuthorsDto(a.id,a.fullName,a.description,a.imageName, count(distinct sub),count( distinct p)," +
            "sum(case when s.id =?1 then 1 else 0 end)) " +
            "from Author a left join a.subscribers s left join a.poems p left join a.subscribers sub " +
            "where s.id =?1 " +
            "group by a")
    Page<AuthorsDto> findAllSubscriptions(Long id,Pageable pageable);


    @QueryHints(
            @QueryHint(name ="org.hibernate.jpa.QueryHints.HINT_PASS_DISTINCT_THROUGH",value = "false"))
    @Query("select distinct new ua.poems_club.dto.author.AuthorsDto(a.id,a.fullName,a.description,a.imageName, count(distinct sub),count( distinct p)," +
            "sum(case when s.id =:id then 1 else 0 end)) " +
            "from Author a left join a.subscribers s left join a.poems p left join a.subscribers sub  " +
            "where s.id =:id and lower(a.fullName) like lower(CONCAT('%',:name,'%'))" +
            "group by a")
    Page<AuthorsDto> findAllSubscriptionsByName(@Param("id")Long id,@Param("name")String name,Pageable pageable);


    @QueryHints(
            @QueryHint(name ="org.hibernate.jpa.QueryHints.HINT_PASS_DISTINCT_THROUGH",value = "false"))
    @Query("select distinct new ua.poems_club.dto.author.AuthorsDto(a.id,a.fullName,a.description,a.imageName, count(distinct s),count( distinct p)," +
            "sum(case when s.id =?1 then 1 else 0 end)) " +
            "from Author a left join a.subscribers s left join a.subscriptions subscriptions left join a.poems p " +
            "where subscriptions.id =?1 " +
            "group by a")
    Page<AuthorsDto> findAllSubscribers(Long id,Pageable pageable);


    @QueryHints(
            @QueryHint(name ="org.hibernate.jpa.QueryHints.HINT_PASS_DISTINCT_THROUGH",value = "false"))
    @Query("select distinct new ua.poems_club.dto.author.AuthorsDto(a.id,a.fullName,a.description,a.imageName, count(distinct s),count(distinct p)," +
            "sum(case when s.id =:id then 1 else 0 end)) " +
            "from Author a left join a.subscribers s left join a.subscriptions subscriptions left join a.poems p " +
            "where subscriptions.id =:id and a.fullName like lower(CONCAT('%',:name,'%')) " +
            "group by a")
    Page<AuthorsDto> findAllSubscribersByName(@Param("id") Long id,@Param("name") String name,Pageable pageable);

}


