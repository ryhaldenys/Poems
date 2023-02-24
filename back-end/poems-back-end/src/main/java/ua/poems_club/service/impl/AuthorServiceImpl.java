package ua.poems_club.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;
import ua.poems_club.builder.AuthorBuilder;
import ua.poems_club.dto.author.*;
import ua.poems_club.exception.AuthorAlreadyExist;
import ua.poems_club.exception.IncorrectAuthorDetailsException;
import ua.poems_club.exception.InvalidImagePathException;
import ua.poems_club.exception.NotFoundException;
import ua.poems_club.model.Author;
import ua.poems_club.repository.AuthorRepository;
import ua.poems_club.security.dto.RegistrationRequestDto;
import ua.poems_club.service.AuthorService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthorServiceImpl implements AuthorService {
    private final AuthorRepository authorRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Value("${upload.path}")
    private String uploadPath;

    @Value("${file.path}")
    private String imagePath;

    @Value("${default.image}")
    private String defaultImage;


    @Override
    public Page<AuthorsDto> getAllAuthors(Long currentAuthorId,Pageable pageable) {
        var authors = getAll(currentAuthorId,pageable);
        setImagePathForAll(authors);
        return authors;
    }

    private Page<AuthorsDto> getAll(Long currentAuthorId, Pageable pageable){
        var authors = authorRepository.findAllAuthors(currentAuthorId,pageable);
        if(authors.getContent().isEmpty()){
            throw new NotFoundException("Cannot find any authors");
        }
        return authors;
    }

    private void setImagePathForAll(Page<AuthorsDto> authors) {
        authors.forEach(this::setImagePath);
    }

    private void setImagePath(AuthorsDto author){
        if (author.getImagePath() == null)
            author.setImagePath(imagePath+uploadPath+"/"+defaultImage);
        else
            author.setImagePath(imagePath+uploadPath+"/"+author.getImagePath());
    }



    @Override
    public AuthorDto getAuthorById(Long id) {
        var author =  getById(id);
        setImagePath(author);
        return author;
    }

    private void setImagePath(AuthorDto author) {
        if (author.getImagePath() == null)
            author.setImagePath(imagePath+uploadPath+"/"+defaultImage);
        else
            author.setImagePath(imagePath+uploadPath+"/"+author.getImagePath());
    }

    private AuthorDto getById(Long id){
        return authorRepository.findAuthorById(id).
                orElseThrow(()->throwNotFoundAuthorById(id));
    }

    @Override
    @Transactional
    public Long createAuthor(RegistrationRequestDto author) {
        return create(author);
    }

    private Long create(RegistrationRequestDto authorDto){
        var author = buildAuthor(authorDto);
        checkAuthorIsExist(author);
        encodePassword(author);
        authorRepository.save(author);
        return author.getId();
    }

    private Author buildAuthor(RegistrationRequestDto authorDto){
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
    public void addAuthorImage(Long id, MultipartFile imageUrl) {
        var author = getAuthor(id);
        addImage(author,imageUrl);
    }

    private void addImage(Author author, MultipartFile multipartFile) {
        try {
            checkIsImagePathNotNull(multipartFile);
            createDirectoryIfNotExist();
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
        if (!Objects.isNull(imageName)){
            FileSystemUtils.deleteRecursively(Path.of(uploadPath+"/"+imageName));
        }
    }

    private void checkIsImagePathNotNull(MultipartFile multipartFile){
        if(multipartFile == null)
            throw new InvalidImagePathException("Gotten file is invalid");
    }

    private void createDirectoryIfNotExist(){
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()){
            uploadDir.mkdir();
        }
    }

    private String generateUniqueFileName(MultipartFile multipartFile){
        String uuidFile = UUID.randomUUID().toString();
        return uuidFile+"."+ multipartFile.getOriginalFilename();
    }

    private void saveImage(MultipartFile multipartFile, String fileName) throws IOException {
        multipartFile.transferTo(new File(uploadPath +"/"+fileName));
    }

    private void setNewImageToAuthor(Author author,String fileName) {
        author.setImageName(fileName);
    }


    @Override
    @Transactional
    public void updateAuthorSubscriptions(Long authorId, Long subscriptionId) {
        var author = getAuthorFetchSubscriptions(authorId);
        var subscription = getAuthorFetchSubscribers(subscriptionId);

        updateAuthorSubscriptions(author,subscription);
    }

    @Override
    @Transactional
    public void deleteImage(Long id) {
        delete(id);
    }

    private void delete(Long id){
        try {
            var author = getAuthor(id);
            deleteOldImageIfItExist(author);
            setNullImageName(author);
        } catch (IOException e) {
            throw new InvalidImagePathException("Invalid image path");
        }
    }

    private void setNullImageName(Author author){
        author.setImageName(null);
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


