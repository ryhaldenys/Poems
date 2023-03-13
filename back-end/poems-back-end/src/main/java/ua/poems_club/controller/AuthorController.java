package ua.poems_club.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ua.poems_club.dto.author.*;
import ua.poems_club.dto.poem.PoemsDto;
import ua.poems_club.model.Author;
import ua.poems_club.security.dto.AuthenticationRequestDto;
import ua.poems_club.security.model.SecurityUser;
import ua.poems_club.security.service.AuthenticationService;
import ua.poems_club.service.AuthorService;

import java.net.URI;

import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.*;

@RestController
@RequestMapping("/api/authors")
@RequiredArgsConstructor
public class AuthorController {
    private final AuthenticationService authenticationService;
    private final AuthorService authorService;

    @GetMapping
    public Page<AuthorsDto> getAll(Pageable pageable, @AuthenticationPrincipal SecurityUser currentUser){
        return authorService.getAllAuthors(currentUser.getId(),pageable);
    }

    @GetMapping("/{id}")
    public AuthorDto getAuthor(@PathVariable Long id){
        return authorService.getAuthorById(id);
    }


    @PutMapping("/{id}")
    public ResponseEntity<?> updateAuthor(@PathVariable Long id, @RequestBody UpdateAuthorDto author){

        var updatedAuthor = authorService.updateAuthor(id,author);
        var response = authenticationService.authenticate(updatedAuthor, author.password());

        var uri = getUriFromCurrentRequest();

        return ResponseEntity.created(uri)
                .body(response);
    }

    @PatchMapping("/{id}/password")
    public ResponseEntity<?> updatePassword(@PathVariable Long id, @RequestBody PasswordDto password){
        authorService.updateAuthorPassword(id,password);

        var uri = getUriFromCurrentRequest();

        return ResponseEntity.status(NO_CONTENT)
                .location(uri)
                .build();
    }

    @PostMapping(value = "/{id}/image",produces = MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(NO_CONTENT)
    public void addImage(@PathVariable Long id,@RequestParam("file") MultipartFile multipartFile){
        authorService.addAuthorImage(id,multipartFile);
    }

    @DeleteMapping(value = "/{id}/image")
    @ResponseStatus(NO_CONTENT)
    public void deleteImage(@PathVariable Long id){
        authorService.deleteImage(id);
    }

    @PatchMapping("{id}/subscriptions/{subscription_id}")
    @ResponseStatus(NO_CONTENT)
    public void updateAuthorSubscriptions(@PathVariable Long id, @PathVariable("subscription_id") Long subscriptionId){
        authorService.updateAuthorSubscriptions(id,subscriptionId);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('simple')")
    public Author deleteAuthor(@PathVariable Long id){
        return authorService.deleteAuthor(id);
    }


    @GetMapping("/{id}/subscriptions")
    public Page<AuthorsDto> getSubscriptions(@PathVariable Long id,Pageable pageable){
        return authorService.getAuthorSubscriptions(id,pageable);
    }

    @GetMapping("/{id}/subscribers")
    public Page<AuthorsDto> getSubscribers(@PathVariable Long id,Pageable pageable){
        return authorService.getAuthorSubscribers(id,pageable);
    }

    @GetMapping("/{id}/likes")
    public Page<PoemsDto> getLikes(@PathVariable Long id,Pageable pageable){
        return authorService.getAuthorLikes(id,pageable);
    }


    private URI getUriFromCurrentRequest(){
        return ServletUriComponentsBuilder.fromCurrentRequestUri()
                .build().toUri();
    }
}




