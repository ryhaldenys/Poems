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

    @Query("select distinct new ua.poems_club.dto.poem.PoemsDto(p.id,p.name,p.text,a.id,a.fullName,count(l)," +
            "sum(case when l.id =?1 then 1 else 0 end))" +
            " from Poem p join p.author a left join p.likes l " +
            "group by p.id,a.fullName")
    Page<PoemsDto> findAllPoems(Long currentUserId,Pageable pageable);

    @QueryHints(
            @QueryHint(name ="org.hibernate.jpa.QueryHints.HINT_PASS_DISTINCT_THROUGH",value = "false"))
    @Query("select distinct new ua.poems_club.dto.poem.PoemsDto(p.id,p.name,p.text,a.id,a.fullName,count(l) ," +
            "sum(case when l.id =:id then 1 else 0 end)) " +
            "from Poem p join p.author a left join p.likes l " +
            "where p.name like CONCAT('%',:name,'%')" +
            "group by p.id,a.fullName")
    Page<PoemsDto> findAllPoemsByName(@Param("id")Long currentUserId,Pageable pageable, @Param("name") String name);

    @Query("select new ua.poems_club.dto.poem.PoemDto(p.id,p.name,p.text,a.id,a.fullName)" +
            " from Poem p join p.author a where p.id =?1")
    Optional<PoemDto> findPoemById(Long authorId);

    @Query("select distinct new ua.poems_club.dto.poem.PoemsDto(p.id,p.name,p.text,a.id,a.fullName,count(l) ," +
            "sum(case when l.id =?2 then 1 else 0 end)) " +
            "from Poem p join p.author a left join p.likes l where a.id =?1 " +
            "group by p.id,a.fullName")
    Page<PoemsDto> findAllByAuthorId(Long authorId,Long currentUserId, Pageable pageable);
}
