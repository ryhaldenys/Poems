package ua.poems_club.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.poems_club.builder.AuthorBuilder;
import ua.poems_club.dto.author.*;
import ua.poems_club.exception.AuthorAlreadyExist;
import ua.poems_club.exception.IncorrectAuthorDetailsException;
import ua.poems_club.exception.NotFoundException;
import ua.poems_club.model.Author;
import ua.poems_club.repository.AuthorRepository;
import ua.poems_club.service.AuthorService;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthorServiceImpl implements AuthorService {
    private final AuthorRepository authorRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public Page<AuthorsDto> getAllAuthors(Long currentAuthorId,Pageable pageable) {
        return getAll(currentAuthorId,pageable);
    }

    private Page<AuthorsDto> getAll(Long currentAuthorId, Pageable pageable){
        var authors = authorRepository.findAllAuthors(currentAuthorId,pageable);
        if(authors.getContent().isEmpty()){
            throw new NotFoundException("Cannot find any authors");
        }
        return authors;
    }

    @Override
    public AuthorDto getAuthorById(Long id) {
        return getById(id);
    }

    private AuthorDto getById(Long id){
        return authorRepository.findAuthorById(id).
                orElseThrow(()->throwNotFoundAuthorById(id));
    }

    @Override
    @Transactional
    public Long createAuthor(CreateAuthorDto author) {
        return create(author);
    }

    private Long create(CreateAuthorDto authorDto){
        var author = buildAuthor(authorDto);
        checkAuthorIsExist(author);
        encodePassword(author);
        authorRepository.save(author);
        return author.getId();
    }

    private Author buildAuthor(CreateAuthorDto authorDto){
        return AuthorBuilder.builder()
                .fullName(authorDto.fullName())
                .email(authorDto.email())
                .imageUrl("default")
                .password(authorDto.password())
                .build();
    }

    private void checkAuthorIsExist(Author author) {
        checkIsAuthorByEmail(author.getEmail());
        checkIsAuthorByFullName(author.getFullName());
    }

    private void checkIsAuthorByEmail(String email){
        if (authorRepository.findByEmail(email).isPresent()){
            throw new AuthorAlreadyExist("Author with email: "+email+", already exist");
        }
    }

    private void encodePassword(Author author){
        var password = author.getPassword();
        author.setPassword(passwordEncoder.encode(password));
    }

    private void checkIsAuthorByFullName(String fullName){
        if (authorRepository.findByFullName(fullName).isPresent()){
            throw new AuthorAlreadyExist("Author with full name: "+fullName+", already exist");
        }
    }

    //todo: add b2 encoding
    //todo: default img


    @Override
    @Transactional
    public void updateAuthor(Long id, UpdateAuthorDto author) {
        var foundAuthor = getAuthor(id);
        checkIsAuthorByEmail(author.email());
        checkIsAuthorByFullName(author.fullName());
        updateAuthor(foundAuthor,author);


    }

    private Author getAuthor(Long id) {
        return authorRepository.findById(id)
                .orElseThrow(()->throwNotFoundAuthorById(id));

    }

    private void updateAuthor(Author foundAuthor, UpdateAuthorDto author) {
        foundAuthor.setFullName(author.fullName());
        foundAuthor.setEmail(author.email());
        foundAuthor.setDescription(author.description());
    }

    @Override
    @Transactional
    public void updateAuthorPassword(Long id, PasswordDto password) {
        var author = getAuthor(id);
        updateAuthorPassword(author,password);
    }

    private void updateAuthorPassword(Author author, PasswordDto password) {
        checkIfOldPasswordIsCorrect(author.getPassword(), password.oldPassword());
        var encodedNewPassword = passwordEncoder.encode(password.newPassword());
        author.setPassword(encodedNewPassword);
    }

    private void checkIfOldPasswordIsCorrect(String authorPassword,String oldPassword){
        if (!passwordEncoder.matches(oldPassword,authorPassword)){
            throw new IncorrectAuthorDetailsException("Entered old password does not equal user password");
        }
    }


    private NotFoundException throwNotFoundAuthorById(Long id){
        return new NotFoundException("Cannot find an author by id: "+id);
    }

    @Override
    @Transactional
    public Author deleteAuthor(Long id) {
        var author = getAuthor(id);
        removeAuthor(id);
        return author;
    }

    private void removeAuthor(Long id) {
        authorRepository.deleteById(id);
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
    @Transactional
    public void updateAuthorImageUrl(Long id,AuthorImageUrlDto imageUrl) {
        var author = getAuthor(id);
        updateImage(author,imageUrl);
    }

    private void updateImage(Author author,AuthorImageUrlDto imageUrl) {
        author.setImageUrl(imageUrl.imageUrl());
    }

    @Override
    @Transactional
    public void updateAuthorSubscriptions(Long authorId, Long subscriptionId) {
        var author = getAuthorFetchSubscriptions(authorId);
        var subscription = getAuthorFetchSubscribers(subscriptionId);

        updateAuthorSubscriptions(author,subscription);
    }

    private void updateAuthorSubscriptions(Author author, Author subscription) {
        if (checkIsSubscription(author, subscription))
            removeSubscription(author, subscription);
        else
            addSubscription(author,subscription);
    }


    private boolean checkIsSubscription(Author author, Author subscription){
        return author.getSubscriptions().contains(subscription);
    }

    private void removeSubscription(Author author, Author subscription) {
        author.removeSubscription(subscription);
    }

    private void addSubscription(Author author, Author subscription) {
        author.addSubscription(subscription);
    }

    private Author getAuthorFetchSubscriptions(Long authorId) {
        return authorRepository.findAuthorFetchSubscriptions(authorId)
                .orElseThrow(()-> throwNotFoundAuthorById(authorId));
    }

    private Author getAuthorFetchSubscribers(Long subscriptionId) {
        return authorRepository.findAuthorFetchSubscribers(subscriptionId)
                .orElseThrow(()->throwNotFoundAuthorById(subscriptionId));
    }
}


