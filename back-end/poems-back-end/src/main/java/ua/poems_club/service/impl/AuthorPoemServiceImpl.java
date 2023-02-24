package ua.poems_club.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.poems_club.builder.PoemBuilder;
import ua.poems_club.dto.poem.RequestPoemDto;
import ua.poems_club.dto.poem.PoemsDto;
import ua.poems_club.exception.NotFoundException;
import ua.poems_club.model.Author;
import ua.poems_club.model.Poem;
import ua.poems_club.repository.AuthorRepository;
import ua.poems_club.repository.PoemRepository;
import ua.poems_club.service.AuthorPoemService;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuthorPoemServiceImpl implements AuthorPoemService {
    private final PoemRepository poemRepository;
    private final AuthorRepository authorRepository;

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

    @Override
    @Transactional
    public void createPoem(Long authorId, RequestPoemDto poem) {
        var newPoem = createInstanceOfPoem(poem);
        savePoem(authorId,newPoem);
    }

    private Poem createInstanceOfPoem(RequestPoemDto poem){
        return PoemBuilder.builder()
                .name(poem.name())
                .text(poem.text())
                .build();
    }

    private void savePoem(Long authorId, Poem newPoem){
        var author = authorRepository.getReferenceById(authorId);
        author.addPoem(newPoem);
    }


    @Override
    @Transactional
    public void updatePoem(Long id, Long poemId, RequestPoemDto request) {
        var poem = getPoemByAuthorIdAndId(id,poemId);
        updatePoem(poem,request);
    }

    private Poem getPoemByAuthorIdAndId(Long id, Long poemId) {
        return poemRepository.findPoemByAuthorIdAndId(id,poemId)
                .orElseThrow(()->new NotFoundException("Cannot find poem by author id: "+id+" and poem id: "+poemId));
    }

    private void updatePoem(Poem poem, RequestPoemDto request) {
        poem.setName(request.name());
        poem.setText(request.text());
    }


    @Override
    @Transactional
    public void deletePoem(Long id, Long poemId) {
        var poem = getPoemByAuthorIdAndIdFetchLikes(id,poemId);
        deletePoem(poem);
    }

    private Poem getPoemByAuthorIdAndIdFetchLikes(Long id, Long poemId) {
        return poemRepository.findPoemByAuthorIdAndIdFetchLikes(id,poemId)
                .orElseThrow(()->new NotFoundException("Cannot find poem by author id: "+id+" and poem id: "+poemId));
    }

    void deletePoem(Poem poem){
        poemRepository.delete(poem);
    }

    @Override
    @Transactional
    public void updatePoemLikes(Long id, Long poemId) {
        var poem = getPoemByIdFetchLikes(poemId);
        var like = getAuthorByIdFetchLikes(id);

        updatePoemLikes(poem,like);

    }

    private Poem getPoemByIdFetchLikes(Long poemId) {
        return poemRepository.findPoemByIdFetchLikes(poemId)
                .orElseThrow(()->new NotFoundException("Cannot find poem by id:"+poemId));
    }

    private void updatePoemLikes(Poem poem, Author like) {
        if (poem.getLikes().contains(like)){
            poem.removeLike(like);
        }else
            poem.addLike(like);
    }

    private Author getAuthorByIdFetchLikes(Long likeId){
        return authorRepository.findByIdFetchLikes(likeId)
                .orElseThrow(()->new NotFoundException("Cannot find author by id: "+likeId));
    }


}
