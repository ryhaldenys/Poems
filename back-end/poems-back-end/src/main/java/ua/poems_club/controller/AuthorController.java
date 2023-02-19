package ua.poems_club.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ua.poems_club.dto.author.*;
import ua.poems_club.model.Author;
import ua.poems_club.service.AuthorService;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/api/authors")
@RequiredArgsConstructor
public class AuthorController {
    private final AuthorService authorService;

    @GetMapping
    @PreAuthorize("hasAuthority('simple')")
    public Page<AuthorsDto> getAll(Pageable pageable){
        return authorService.getAllAuthors(pageable);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('simple')")
    public AuthorDto getAuthor(@PathVariable Long id){
        return authorService.getAuthorById(id);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('simple')")
    public ResponseEntity<?> createAccount(@RequestBody CreateAuthorDto author){

        var authorId = authorService.createAuthor(author);

        var uri = ServletUriComponentsBuilder.fromCurrentRequestUri()
                .path("/{id}").buildAndExpand(authorId).toUri();

        return ResponseEntity.status(CREATED)
                .location(uri)
                .build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('simple')")
    public ResponseEntity<?> updateAuthor(@PathVariable Long id, @RequestBody UpdateAuthorDto author){
        authorService.updateAuthor(id,author);

        var uri = ServletUriComponentsBuilder.fromCurrentRequestUri()
                .build().toUri();

        return ResponseEntity.status(NO_CONTENT)
                .location(uri)
                .build();
    }


    @PatchMapping("/{id}")
    @PreAuthorize("hasAuthority('simple')")
    public ResponseEntity<?> updatePassword(@PathVariable Long id, @RequestBody PasswordDto password){
        authorService.updateAuthorPassword(id,password);
        var uri = ServletUriComponentsBuilder.fromCurrentRequestUri()
                .build().toUri();

        return ResponseEntity.status(NO_CONTENT)
                .location(uri)
                .build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('simple')")
    public Author deleteAuthor(@PathVariable Long id){
        return authorService.deleteAuthor(id);
    }
}


