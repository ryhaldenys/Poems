package ua.poems_club.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.poems_club.dto.AuthorDto;
import ua.poems_club.dto.AuthorsDto;
import ua.poems_club.exception.NotFoundException;
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
}
