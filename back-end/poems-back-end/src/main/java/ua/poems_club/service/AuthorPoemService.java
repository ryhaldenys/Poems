package ua.poems_club.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ua.poems_club.dto.poem.RequestPoemDto;
import ua.poems_club.dto.poem.PoemsDto;

public interface AuthorPoemService {
    Page<PoemsDto> getAllByAuthorId(Long authorId,Long currentUserId, Pageable pageable);
    void createPoem(Long authorId, RequestPoemDto poem);
    void updatePoem(Long id, Long poemId, RequestPoemDto poem);

    void deletePoem(Long id, Long poemId);

    void updatePoemLikes(Long id, Long poemId);
}
