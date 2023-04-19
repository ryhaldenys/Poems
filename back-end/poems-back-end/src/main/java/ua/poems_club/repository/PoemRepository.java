package ua.poems_club.repository;

import jakarta.persistence.QueryHint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import ua.poems_club.dto.poem.PoemDto;
import ua.poems_club.dto.poem.PoemsDto;
import ua.poems_club.model.Poem;


import java.util.Optional;


public interface PoemRepository extends JpaRepository<Poem,Long> {

    @Query("select new ua.poems_club.dto.poem.PoemsDto(p.id,p.name,p.text,a.id,p.status,a.fullName,count(l)," +
            "sum(case when l.id =:id then 1 else 0 end))" +
            " from Poem p join p.author a left join p.likes l where p.status = 'PUBLIC' " +
            "group by p.id,a.fullName ")
    Page<PoemsDto> findAllPoems(@Param("id") Long currentUserId,Pageable pageable);


    @Query("select new ua.poems_club.dto.poem.PoemsDto(p.id,p.name,p.text,a.id,p.status,a.fullName,count(l) ," +
            "sum(case when l.id =:id then 1 else 0 end)) " +
            "from Poem p join p.author a left join p.likes l " +
            "where (lower(p.name) like lower(CONCAT('%',:name,'%')) or " +
            "lower(p.text) like lower(CONCAT('%',:name,'%'))) and p.status ='PUBLIC' " +
            "group by p.id,a.fullName ")
    Page<PoemsDto> findAllPoemsWhichContainText(@Param("id")Long currentUserId, Pageable pageable, @Param("name") String name);

    @Query("select new ua.poems_club.dto.poem.PoemDto(p.id,p.name,p.text,p.status,a.id,a.fullName)" +
            " from Poem p join p.author a where p.id =?1")
    Optional<PoemDto> findPoemById(Long authorId);

    @Query("select distinct new ua.poems_club.dto.poem.PoemsDto(p.id,p.name,p.text,a.id,p.status,a.fullName,count(l) ," +
            "sum(case when l.id =?2 then 1 else 0 end)) " +
            "from Poem p join p.author a left join p.likes l where a.id =?1 and p.status = 'PUBLIC' " +
            "group by p.id,a.fullName")
    Page<PoemsDto> findAllPublicPoemsByAuthorId(Long authorId, Long currentUserId, Pageable pageable);

    @Query("select distinct new ua.poems_club.dto.poem.PoemsDto(p.id,p.name,p.text,a.id,p.status,a.fullName,count(l) ," +
            "sum(case when l.id =:currentAuthorId then 1 else 0 end)) " +
            "from Poem p join p.author a left join p.likes l " +
            "where a.id =:authorId and p.status = 'PUBLIC' and (lower(p.name) like lower(CONCAT('%',:name,'%'))" +
            "or lower(p.text) like lower(CONCAT('%',:name,'%'))) " +
            "group by p.id,a.fullName")
    Page<PoemsDto> findAllPublicPoemsByAuthorIdWhichContainsText(@Param("authorId") Long authorId, @Param("currentAuthorId") Long currentUserId,
                                                                 @Param("name") String poemName, Pageable pageable);



    @Query("select new ua.poems_club.dto.poem.PoemsDto(p.id,p.name,p.text,a.id,p.status,a.fullName,count(l) ," +
            "sum(case when l.id =?2 then 1 else 0 end)) " +
            "from Poem p join p.author a left join p.likes l where a.id =?1 " +
            "group by p.id,a.fullName")
    Page<PoemsDto> findAllPoemsByAuthorId(Long authorId, Long currentUserId, Pageable pageable);


    @Query("select p from Poem p where p.author.id =?1 and p.id=?2")
    Optional<Poem> findPoemByAuthorIdAndId(Long id, Long poemId);

    @QueryHints(
            @QueryHint(name ="org.hibernate.jpa.QueryHints.HINT_PASS_DISTINCT_THROUGH",value = "false"))
    @Query("select distinct p from Poem p left join fetch  p.likes join p.author a where a.id =?1 and p.id=?2")
    Optional<Poem> findPoemByAuthorIdAndIdFetchLikes(Long id, Long poemId);


    @QueryHints(
            @QueryHint(name ="org.hibernate.jpa.QueryHints.HINT_PASS_DISTINCT_THROUGH",value = "false"))
    @Query("select distinct p from Poem p left join fetch p.likes where p.id =?1")
    Optional<Poem> findPoemByIdFetchLikes(Long poemId);

    @Query("select distinct new ua.poems_club.dto.poem.PoemsDto(p.id,p.name,p.text,a.id,p.status,a.fullName,count(l)," +
            "sum(case when l.id =:id then 1 else 0 end)) " +
            "from Poem p join p.author a left join p.likes l " +
            "where p.status = 'PUBLIC' and l.id =:id " +
            "group by p.id,a.fullName")
    Page<PoemsDto> findAllAuthorLikes(@Param("id") Long currentUserId,Pageable pageable);

    @Query("select distinct new ua.poems_club.dto.poem.PoemsDto(p.id,p.name,p.text,a.id,p.status,a.fullName,count(l)," +
            "sum(case when l.id =:id then 1 else 0 end)) " +
            "from Poem p join p.author a left join p.likes l " +
            "where (lower(p.name) like lower(CONCAT('%',:name,'%')) or " +
            "lower(p.text) like lower(CONCAT('%',:name,'%'))) and p.status = 'PUBLIC' and l.id =:id " +
            "group by p.id,a.fullName")
    Page<PoemsDto> findAllAuthorLikesWhichContainText(@Param("id") Long currentUserId, @Param("name") String name, Pageable pageable);


}
