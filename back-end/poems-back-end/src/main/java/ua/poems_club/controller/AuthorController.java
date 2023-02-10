package ua.poems_club.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.poems_club.model.Author;
import ua.poems_club.repository.AuthorRepository;

@RestController
@RequestMapping("api/authors")
@RequiredArgsConstructor
public class AuthorController {
    private final AuthorRepository authorRepository;

    @GetMapping
    public Page<Author> getAll(Pageable pageable){
        return authorRepository.findAll(pageable);
    }

}
