package ua.poems_club.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ua.poems_club.builder.AuthorBuilder;
import ua.poems_club.dto.author.PasswordDto;
import ua.poems_club.dto.author.UpdateAuthorDto;
import ua.poems_club.exception.AuthorAlreadyExist;
import ua.poems_club.exception.IncorrectAuthorDetailsException;
import ua.poems_club.exception.InvalidImagePathException;
import ua.poems_club.exception.NotFoundException;
import ua.poems_club.model.Author;
import ua.poems_club.model.Poem;
import ua.poems_club.repository.AuthorRepository;
import ua.poems_club.security.dto.RegistrationRequestDto;
import ua.poems_club.service.AmazonImageService;
import ua.poems_club.service.ManagementAuthorService;

import java.io.IOException;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ManagementAuthorServiceImpl implements ManagementAuthorService {
    private final AuthorRepository authorRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AmazonImageService amazonImageService;

    @Value("${DEFAULT_IMG_NAME}")
    private String defaultImage;

    @Override
    @Transactional
    @CacheEvict(value = "authors",allEntries = true)
    public Author createAuthor(RegistrationRequestDto author) {
        return create(author);
    }

    private Author create(RegistrationRequestDto authorDto){
        var author = buildAuthor(authorDto);
        checkAuthorIsExist(author);
        encodePassword(author);

        return authorRepository.save(author);
    }

    private Author buildAuthor(RegistrationRequestDto authorDto){
        return AuthorBuilder.builder()
                .fullName(authorDto.fullName())
                .email(authorDto.email())
                .imageUrl(defaultImage)
                .password(authorDto.password())
                .build();
    }

    private void checkAuthorIsExist(Author author) {
        checkIsAuthorWithEmail(author.getEmail());
        checkIsAuthorWithFullName(author.getFullName());
    }

    private void checkIsAuthorWithEmail(String email){
        if (authorRepository.findByEmail(email).isPresent()){
            throw new AuthorAlreadyExist("Author with email: "+email+", already exist");
        }
    }

    private void checkIsAuthorWithFullName(String fullName){
        if (authorRepository.findByFullName(fullName).isPresent()){
            throw new AuthorAlreadyExist("Author with full name: "+fullName+", already exist");
        }
    }

    private void encodePassword(Author author){
        var password = author.getPassword();
        author.setPassword(passwordEncoder.encode(password));
    }



    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "authors",allEntries = true),
            @CacheEvict(value = "author",key = "#id")
    })
    public Author updateAuthor(Long id, UpdateAuthorDto author) {
        var foundAuthor = getAuthor(id);
        checkIsOtherAuthorWithFullName(foundAuthor,author);
        checkIsOtherAuthorWithEmail(foundAuthor,author);
        return updateAuthor(foundAuthor,author);
    }

    private Author getAuthor(Long id) {
        return authorRepository.findById(id)
                .orElseThrow(()->throwNotFoundAuthorById(id));
    }

    private void checkIsOtherAuthorWithFullName(Author foundAuthor, UpdateAuthorDto author) {
        if(!foundAuthor.getFullName().equals(author.fullName()))
            checkIsAuthorWithFullName(author.fullName());
    }

    private void checkIsOtherAuthorWithEmail(Author foundAuthor, UpdateAuthorDto author) {
        if(!foundAuthor.getEmail().equals(author.email()))
            checkIsAuthorWithEmail(author.email());
    }

    private Author updateAuthor(Author foundAuthor, UpdateAuthorDto author) {
        foundAuthor.setFullName(author.fullName());
        foundAuthor.setEmail(author.email());
        foundAuthor.setDescription(author.description());
        return foundAuthor;
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
    @CacheEvict(value = "authors",allEntries = true)
    public Author deleteAuthor(Long id) {
        var author = getAuthorFetchAllFields(id);
        deleteAuthorImage(author);
        deleteSubscribers(author);
        deleteSubscriptions(author);
        deletePoemsWithLikes(author);
        deleteMyLikes(author);
        deleteAuthorById(id);
        return author;
    }

    private void deleteAuthorImage(Author author) {
        var image =author.getImageName();
        if (!image.equals(defaultImage))
            amazonImageService.deleteImage(image);
    }

    private Author getAuthorFetchAllFields(Long id){
        return authorRepository.findAuthorByIdFetchAllFields(id)
                .orElseThrow();
    }

    private void deleteSubscribers(Author author) {
        var subscribers = author.getSubscribers();
        author.removeAllSubscribers(subscribers);
    }

    private void deleteSubscriptions(Author author) {
        var subscribers = author.getSubscriptions();
        author.removeAllSubscriptions(subscribers);
    }

    private void deletePoemsWithLikes(Author author) {
        var poems = author.getPoems();
        poems.forEach(this::deletePoemLikes);
        author.removeAllPoems(poems);
    }

    private void deletePoemLikes(Poem poem) {
        var likes = poem.getLikes();
        poem.removeAllLikes(likes);
    }
    private void deleteMyLikes(Author author) {
        var myLikes = author.getMyLikes();
        author.removeAllMyLikes(myLikes);
    }

    private void deleteAuthorById(Long id){
        authorRepository.deleteById(id);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "authors",allEntries = true),
            @CacheEvict(value = "author",key = "#id")
    })
    public void addAuthorImage(Long id, MultipartFile imageUrl) {
        var author = getAuthor(id);
        addImage(author,imageUrl);
    }

    private void addImage(Author author, MultipartFile multipartFile) {
        try {
            checkIsImagePathNotNull(multipartFile);
            deleteOldImageIfItExist(author);

            String fileName = generateUniqueFileName(multipartFile);

            saveImage(multipartFile,fileName);
            setNewImageToAuthor(author,fileName);

        } catch (IOException e) {
            throw new InvalidImagePathException("Invalid image path");
        }
    }

    private void deleteOldImageIfItExist(Author author) throws IOException {
        var imageName = author.getImageName();
        if (!imageName.equals(defaultImage)){
            amazonImageService.deleteImage(imageName);
        }
    }

    private void checkIsImagePathNotNull(MultipartFile multipartFile){
        if(multipartFile == null)
            throw new InvalidImagePathException("Gotten file is invalid");
    }

    private String generateUniqueFileName(MultipartFile multipartFile){
        String uuidFile = UUID.randomUUID().toString();
        return uuidFile+"."+ multipartFile.getOriginalFilename();
    }

    private void saveImage(MultipartFile multipartFile, String fileName) throws IOException {
        amazonImageService.saveImage(fileName, multipartFile);
    }

    private void setNewImageToAuthor(Author author,String fileName) {
        author.setImageName(fileName);
    }


    @Override
    @Transactional
    @Caching(evict = {
        @CacheEvict(value = "author", key = "#authorId"),
        @CacheEvict(value = "authors",allEntries = true)
    })
    public void updateAuthorSubscriptions(Long authorId, Long subscriptionId) {
        var author = getAuthorFetchSubscriptions(authorId);
        var subscription = getAuthorFetchSubscribers(subscriptionId);

        updateAuthorSubscriptions(author,subscription);
    }

    private Author getAuthorFetchSubscriptions(Long authorId) {
        return authorRepository.findAuthorFetchSubscriptions(authorId)
                .orElseThrow(()-> throwNotFoundAuthorById(authorId));
    }

    private Author getAuthorFetchSubscribers(Long subscriptionId) {
        return authorRepository.findAuthorFetchSubscribers(subscriptionId)
                .orElseThrow(()->throwNotFoundAuthorById(subscriptionId));
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

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "authors", allEntries = true),
            @CacheEvict(value = "author",key = "#id")
    })
    public void deleteImage(Long id) {
        delete(id);
    }

    private void delete(Long id){
        try {
            var author = getAuthor(id);
            deleteOldImageIfItExist(author);
            setDefaultImageName(author);
        } catch (IOException e) {
            throw new InvalidImagePathException("Invalid image path");
        }
    }

    private void setDefaultImageName(Author author){
        author.setImageName(defaultImage);
    }

}
