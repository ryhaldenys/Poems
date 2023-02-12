package ua.poems_club.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.poems_club.dto.AuthorDto;
import ua.poems_club.dto.AuthorsDto;
import ua.poems_club.service.AuthorService;

@RestController
@RequestMapping("/api/authors")
@RequiredArgsConstructor
public class AuthorController {
    private final AuthorService authorService;

    @GetMapping
    public Page<AuthorsDto> getAll(Pageable pageable){
        return authorService.getAllAuthors(pageable);
    }

    @GetMapping("/{id}")
    public AuthorDto getAuthor(@PathVariable Long id){
        return authorService.getAuthorById(id);
    }

}
