package ua.poems_club.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.poems_club.dto.poem.PoemsDto;
import ua.poems_club.exception.NotFoundException;
import ua.poems_club.repository.PoemRepository;
import ua.poems_club.service.AuthorPoemService;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuthorPoemServiceImpl implements AuthorPoemService {
    private final PoemRepository poemRepository;

    @Override
    public Page<PoemsDto> getAllByAuthorId(Long authorId,Long currentUserId, Pageable pageable) {
        return getAll(authorId,currentUserId,pageable);
    }

    private Page<PoemsDto> getAll(Long id,Long currentUserId, Pageable pageable) {
        var poems =  poemRepository.findAllByAuthorId(id,currentUserId,pageable);
        checkPoemsPageIsNotEmpty(poems);
        return poems;
    }

    private void checkPoemsPageIsNotEmpty(Page<PoemsDto> poems) {
        if (!poems.hasContent()){
            throw new NotFoundException("Cannot find any poems");
        }
    }

}
