package ua.poems_club.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ua.poems_club.dto.author.*;
import ua.poems_club.dto.poem.PoemsDto;

import ua.poems_club.exception.NotFoundException;
import ua.poems_club.model.Author;

import ua.poems_club.repository.AuthorRepository;
import ua.poems_club.repository.PoemRepository;

import ua.poems_club.service.GettingDataAuthorService;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GettingDataAuthorServiceImpl implements GettingDataAuthorService {
    private final AuthorRepository authorRepository;

    @Value("${upload.path}")
    private String uploadPath;

    @Value("${file.path}")
    private String imagePath;

    @Value("${default.image}")
    private String defaultImage;

    private final PoemRepository poemRepository;


    @Override
    public Page<AuthorsDto> getAllAuthors(Long currentAuthorId,String authorName,Pageable pageable) {
        var authors = getAuthors(currentAuthorId, authorName, pageable);
        setImagePathForAll(authors);
        return authors;
    }

    @Override
    public Page<AuthorsDto> getAuthorsSortedBySubscribers(Long id,Pageable pageable) {
        var authors = getAllSortedBySubscribers(id,pageable);
        setImagePathForAll(authors);
        return authors;
    }

    private Page<AuthorsDto> getAllSortedBySubscribers(Long id, Pageable pageable){
        var authors = authorRepository.findAuthorsSortedBySubscribers(id,pageable);
        checkAreAuthors(authors);
        return authors;
    }

    private Page<AuthorsDto> getAuthors(Long currentAuthorId, String authorName, Pageable pageable){
        return authorName.isEmpty() ? getAll(currentAuthorId,pageable):
                getAllByName(currentAuthorId, authorName, pageable);
    }

    private Page<AuthorsDto> getAll(Long currentAuthorId, Pageable pageable){
        var authors = authorRepository.findAllAuthors(currentAuthorId,pageable);
        checkAreAuthors(authors);
        return authors;
    }

    private Page<AuthorsDto> getAllByName(Long currentAuthorId,String authorName, Pageable pageable) {
        var authors = authorRepository.findAllAuthorsByAuthorName(currentAuthorId,authorName,pageable);
        checkAreAuthors(authors);
        return authors;
    }
    private void setImagePathForAll(Page<AuthorsDto> authors) {
        authors.getContent().forEach(this::setImagePath);
    }

    private void setImagePath(AuthorsDto author){
        if (Objects.isNull(author.getImagePath()))
            author.setImagePath(imagePath+uploadPath+"/"+defaultImage);
        else
            author.setImagePath(imagePath+uploadPath+"/"+author.getImagePath());
    }

    @Override
    public AuthorDto getAuthorById(Long id) {
        var author = getById(id);
        setImagePath(author);
        return author;
    }

    private AuthorDto getById(Long id){
        return authorRepository.findAuthorById(id).
                orElseThrow(()->throwNotFoundAuthorById(id));
    }

    private void setImagePath(AuthorDto author) {
        author.setImagePath(imagePath+uploadPath+"/"+author.getImagePath());
    }


    private NotFoundException throwNotFoundAuthorById(Long id){
        return new NotFoundException("Cannot find an author by id: "+id);
    }


    @Override
    public Author getAuthorByEmail(String email) {
        return getByEmail(email);
    }

    private Author getByEmail(String email){
        return authorRepository.findByEmail(email)
                .orElseThrow(()-> new NotFoundException("Cannot find author by email: "+email));
    }


    @Override
    public Page<AuthorsDto> getAuthorSubscriptions(Long id,String authorName,Pageable pageable) {
        return getSubscriptions(id,authorName,pageable);
    }

    private Page<AuthorsDto> getSubscriptions(Long id,String authorName,Pageable pageable) {
        var subscriptions = authorName.isEmpty() ? getAllSubscriptions(id, pageable):
                getAllSubscriptionsByName(id, authorName, pageable);
        setImagePathForAll(subscriptions);

        return subscriptions;
    }


    private Page<AuthorsDto> getAllSubscriptions(Long id,Pageable pageable){
        var subscriptions = authorRepository.findAllSubscriptions(id,pageable);
        checkAreAuthors(subscriptions);
        return subscriptions;
    }

    private Page<AuthorsDto> getAllSubscriptionsByName(Long id,String authorName,Pageable pageable){
        var subscriptions = authorRepository.findAllSubscriptionsByName(id,authorName,pageable);
        checkAreAuthors(subscriptions);
        return subscriptions;
    }


    @Override
    public Page<AuthorsDto> getAuthorSubscribers(Long id,String authorName,Pageable pageable) {
        return getSubscribers(id,authorName,pageable);
    }

    private Page<AuthorsDto> getSubscribers(Long id,String authorName,Pageable pageable){
        var subscriptions = authorName.isEmpty() ? getAllSubscribers(id, pageable):
                getAllSubscribersByName(id, authorName, pageable);
        setImagePathForAll(subscriptions);
        return subscriptions;
    }

    private Page<AuthorsDto> getAllSubscribers(Long id,Pageable pageable){
        var subscriptions = authorRepository.findAllSubscribers(id,pageable);
        checkAreAuthors(subscriptions);
        return subscriptions;
    }


    private Page<AuthorsDto> getAllSubscribersByName(Long id,String authorName,Pageable pageable){
        var subscriptions = authorRepository.findAllSubscribersByName(id,authorName,pageable);
        checkAreAuthors(subscriptions);
        return subscriptions;
    }

    private void checkAreAuthors(Page<AuthorsDto> authors) {
        if(authors.getContent().isEmpty()){
            throw new NotFoundException("Cannot find any authors");
        }
    }

    @Override
    public Page<PoemsDto> getAuthorLikes(Long id,String poemName,Pageable pageable) {
        return getLikes(id,poemName,pageable);
    }

    private Page<PoemsDto> getLikes(Long id,String poemName,Pageable pageable){
        var likes = poemName.isEmpty() ? getAllLikes(id, pageable):
                getAllLikesByWhichContainText(id, poemName, pageable);
        checkArePoems(likes);
        return likes;
    }

    private Page<PoemsDto> getAllLikes(Long id,Pageable pageable){
        var likes = poemRepository.findAllAuthorLikes(id,pageable);
        checkArePoems(likes);
        return likes;
    }

    private Page<PoemsDto> getAllLikesByWhichContainText(Long id, String poemName, Pageable pageable){
        var likes = poemRepository.findAllAuthorLikesWhichContainText(id,poemName,pageable);
        checkArePoems(likes);
        return likes;
    }


    private void checkArePoems(Page<PoemsDto> poems){
        if(poems.getContent().isEmpty()){
            throw new NotFoundException("Cannot find any poems");
        }
    }
}
