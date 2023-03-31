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

import java.util.Arrays;

import static ua.poems_club.model.Poem.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuthorPoemServiceImpl implements AuthorPoemService {
    private final PoemRepository poemRepository;
    private final AuthorRepository authorRepository;

    @Override
    public Page<PoemsDto> getAllPublicPoemsByAuthorIdAndContainText(Long authorId, Long currentUserId, String poemName, Pageable pageable) {
        return getAllPublicPoems(authorId, currentUserId, poemName, pageable);
    }

    private Page<PoemsDto> getAllPublicPoems(Long authorId, Long currentUserId,String poemName, Pageable pageable){
        return poemName.isEmpty()? getPublicPoemsByAuthorId(authorId, currentUserId, pageable):
                getPublicPoemsByAuthorIdWhichContainText(authorId, currentUserId, poemName, pageable);
    }

    private Page<PoemsDto> getPublicPoemsByAuthorId(Long id, Long currentUserId, Pageable pageable) {
        var poems =  poemRepository.findAllPublicPoemsByAuthorId(id,currentUserId,pageable);
        checkPoemsPageIsNotEmpty(poems);
        return poems;
    }

    private Page<PoemsDto> getPublicPoemsByAuthorIdWhichContainText(Long id, Long currentUserId, String poemName, Pageable pageable) {
        var poems =  poemRepository.findAllPublicPoemsByAuthorIdWhichContainsText(id,currentUserId,poemName,pageable);
        checkPoemsPageIsNotEmpty(poems);
        return poems;
    }

    private void checkPoemsPageIsNotEmpty(Page<PoemsDto> poems) {
        if (!poems.hasContent()){
            throw new NotFoundException("Cannot find any poems");
        }
    }


    @Override
    public Page<PoemsDto> getAllPoemsByAuthorId(Long authorId, Long currentUserId, Pageable pageable) {
        return getAllPoems(authorId, currentUserId, pageable);
    }

    private Page<PoemsDto> getAllPoems(Long authorId, Long currentUserId, Pageable pageable){
        var poems = poemRepository.findAllPoemsByAuthorId(authorId,currentUserId,pageable);
        checkPoemsPageIsNotEmpty(poems);
        return poems;
    }


    @Override
    @Transactional
    public void createPoem(Long authorId, RequestPoemDto poem) {
        var newPoem = createInstanceOfPoem(poem);
        savePoem(authorId,newPoem);
    }

    private Poem createInstanceOfPoem(RequestPoemDto poem){
        var status = getStatus(poem.status());
        return PoemBuilder.builder()
                .name(poem.name())
                .text(poem.text())
                .status(status)
                .build();
    }

    private Status getStatus(String status){
        if (statusIsCorrect(status))
             return Status.valueOf(status);

        return Status.PRIVATE;
    }

    private boolean statusIsCorrect(String status){
        return Arrays.stream(Status.values()).anyMatch(s-> s.name().equals(status));
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
        var status  = getStatus(request.status());
        poem.setStatus(status);
    }


    @Override
    @Transactional
    public void deletePoem(Long id, Long poemId) {
        var poem = getPoemByAuthorIdAndIdFetchLikes(id,poemId);
        deleteLikes(poem);
        deletePoem(poemId);
    }

    private void deleteLikes(Poem poem) {
        var likes = poem.getLikes();
        poem.removeAllLikes(likes);
    }

    private Poem getPoemByAuthorIdAndIdFetchLikes(Long id, Long poemId) {
        return poemRepository.findPoemByAuthorIdAndIdFetchLikes(id,poemId)
                .orElseThrow(()->new NotFoundException("Cannot find poem by author id: "+id+" and poem id: "+poemId));
    }

    void deletePoem(Long poemId){
        poemRepository.deleteById(poemId);
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

    private Author getAuthorByIdFetchLikes(Long likeId){
        return authorRepository.findByIdFetchLikes(likeId)
                .orElseThrow(()->new NotFoundException("Cannot find author by id: "+likeId));
    }

    private void updatePoemLikes(Poem poem, Author like) {
        if (poem.getLikes().contains(like)){
            poem.removeLike(like);
        }else
            poem.addLike(like);
    }
}
