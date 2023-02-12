package ua.poems_club.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.poems_club.dto.AuthorDto;
import ua.poems_club.dto.AuthorsDto;
import ua.poems_club.exception.AuthorAlreadyExist;
import ua.poems_club.exception.NotFoundException;
import ua.poems_club.model.Author;
import ua.poems_club.repository.AuthorRepository;
import ua.poems_club.service.AuthorService;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthorServiceImpl implements AuthorService {
    private final AuthorRepository authorRepository;

    @Override
    public Page<AuthorsDto> getAllAuthors(Pageable pageable) {
        return getAll(pageable);
    }

    private Page<AuthorsDto> getAll(Pageable pageable){
        var authors = authorRepository.findAllAuthors(pageable);
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
                orElseThrow(()-> new NotFoundException("Cannot find an author by id: "+id));
    }

    @Override
    @Transactional
    public Author createAuthor(Author author) {
        return create(author);
    }

    private Author create(Author author){
        checkAuthorIsExist(author);
        return authorRepository.save(author);
    }

    private void checkAuthorIsExist(Author author) {
        checkIsAuthorByEmail(author.getEmail());
        checkIsAuthorByFullName(author.getFullName());
    }

    private void checkIsAuthorByEmail(String email){
        if (authorRepository.findAuthorByEmail(email).isPresent()){
            throw new AuthorAlreadyExist("Author with this email already exist");
        }
    }

    private void checkIsAuthorByFullName(String fullName){
        if (authorRepository.findAuthorByFullName(fullName).isPresent()){
            throw new AuthorAlreadyExist("Author with this first name and last name already exist");
        }
    }

    //todo: add b2 encoding
    //todo: default img
}
