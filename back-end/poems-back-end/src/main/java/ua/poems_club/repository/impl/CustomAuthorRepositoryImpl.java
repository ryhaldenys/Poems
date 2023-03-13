package ua.poems_club.repository.impl;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ua.poems_club.model.Author;
import ua.poems_club.repository.CustomAuthorRepository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CustomAuthorRepositoryImpl implements CustomAuthorRepository {
    private final EntityManager entityManager;

    @Override
    public Optional<Author> findAuthorByIdFetchAllFields(Long id) {
        var author = getAuthorFetchPoems(id);
        author = getAuthorFetchSubscribers(author);
        author = getAuthorFetchSubscriptions(author);
        author = getAuthorFetchMyLikes(author);

        return Optional.of(author);
    }

    private Author getAuthorFetchPoems(Long id){
        return entityManager.createQuery("select a from Author a left join fetch a.poems p left join fetch p.likes where a.id =:id", Author.class)
                .setParameter("id",id)
                .getSingleResult();
    }

    private Author getAuthorFetchSubscribers(Author author){
        return entityManager.createQuery("select a from Author a left join fetch a.subscribers where a in :author", Author.class)
                .setParameter("author",author)
                .getSingleResult();
    }

    private Author getAuthorFetchSubscriptions(Author author){
        return entityManager.createQuery("select a from Author a left join fetch a.subscriptions where a in :author", Author.class)
                .setParameter("author",author)
                .getSingleResult();
    }

    private Author getAuthorFetchMyLikes(Author author){
        return entityManager.createQuery("select a from Author a left join fetch a.myLikes where a in :author", Author.class)
                .setParameter("author",author)
                .getSingleResult();
    }

}
