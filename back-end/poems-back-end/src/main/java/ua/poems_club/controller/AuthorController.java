package ua.poems_club.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ua.poems_club.dto.AuthorDto;
import ua.poems_club.dto.AuthorsDto;
import ua.poems_club.dto.CreateAuthorDto;
import ua.poems_club.dto.UpdateAuthorDto;
import ua.poems_club.model.Author;
import ua.poems_club.service.AuthorService;

import static org.springframework.http.HttpStatus.*;

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

    @PostMapping
    public ResponseEntity<?> createAccount(@RequestBody CreateAuthorDto author){

        var createdAuthor = authorService.createAuthor(author);

        var uri = ServletUriComponentsBuilder.fromCurrentRequestUri()
                .path("/{id}").buildAndExpand(createdAuthor.getId()).toUri();

        return ResponseEntity.status(CREATED)
                .location(uri)
                .build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateAuthor(@PathVariable Long id, @RequestBody UpdateAuthorDto author){
        authorService.updateAuthor(id,author);

        var uri = ServletUriComponentsBuilder.fromCurrentRequestUri()
                .build().toUri();

        return ResponseEntity.status(NO_CONTENT)
                .location(uri)
                .build();
    }

}
