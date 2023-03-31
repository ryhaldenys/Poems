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
import ua.poems_club.security.dto.AuthenticationResponseDto;
import ua.poems_club.security.model.SecurityUser;
import ua.poems_club.security.service.AuthenticationService;
import ua.poems_club.service.GettingDataAuthorService;
import ua.poems_club.service.ManipulationAuthorService;

import java.net.URI;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/api/authors")
@RequiredArgsConstructor
public class AuthorController {
    private final AuthenticationService authenticationService;
    private final GettingDataAuthorService gettingDataAuthorService;
    private final ManipulationAuthorService manipulationAuthorService;

    @GetMapping
    public Page<AuthorsDto> getAll(Pageable pageable, @AuthenticationPrincipal SecurityUser currentUser,
                                   @RequestParam(defaultValue = "",name ="name") String authorName){
        return gettingDataAuthorService.getAllAuthors(currentUser.getId(),authorName,pageable);
    }


    @GetMapping("/most-popular")
    public Page<AuthorsDto> getAllSortedBySubscribers(Pageable pageable, @AuthenticationPrincipal SecurityUser currentUser){
        return gettingDataAuthorService.getAuthorsSortedBySubscribers(currentUser.getId(),pageable);
    }



    @GetMapping("/{id}")
    public AuthorDto getAuthor(@PathVariable Long id){
        return gettingDataAuthorService.getAuthorById(id);
    }



    @PutMapping("/{id}")
    public ResponseEntity<?> updateAuthor(@PathVariable Long id, @RequestBody UpdateAuthorDto author){
        var updatedAuthor = manipulationAuthorService.updateAuthor(id,author);
        var token = authenticationService.createToken(updatedAuthor);

        var response = new AuthenticationResponseDto(id,token);
        var uri = getUriFromCurrentRequest();

        return ResponseEntity.created(uri)
                .body(response);
    }

    @PatchMapping("/{id}/password")
    public ResponseEntity<?> updatePassword(@PathVariable Long id, @RequestBody PasswordDto password){
        manipulationAuthorService.updateAuthorPassword(id,password);

        var uri = getUriFromCurrentRequest();

        return ResponseEntity.status(NO_CONTENT)
                .location(uri)
                .build();
    }

    @PostMapping(value = "/{id}/image")
    @ResponseStatus(NO_CONTENT)
    public void addImage(@PathVariable Long id,@RequestPart("file") MultipartFile multipartFile){
        manipulationAuthorService.addAuthorImage(id,multipartFile);
    }

    @DeleteMapping(value = "/{id}/image")
    @ResponseStatus(NO_CONTENT)
    public void deleteImage(@PathVariable Long id){
        manipulationAuthorService.deleteImage(id);
    }

    @PatchMapping("{id}/subscriptions/{subscription_id}")
    @ResponseStatus(NO_CONTENT)
    public void updateAuthorSubscriptions(@PathVariable Long id, @PathVariable("subscription_id") Long subscriptionId){
        manipulationAuthorService.updateAuthorSubscriptions(id,subscriptionId);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('simple')")
    public Author deleteAuthor(@PathVariable Long id){
        return manipulationAuthorService.deleteAuthor(id);
    }


    @GetMapping("/{id}/subscriptions")
    public Page<AuthorsDto> getSubscriptions(@PathVariable Long id,Pageable pageable,
                                             @RequestParam(defaultValue = "",name = "name")String authorName){
        return gettingDataAuthorService.getAuthorSubscriptions(id,authorName,pageable);
    }

    @GetMapping("/{id}/subscribers")
    public Page<AuthorsDto> getSubscribers(@PathVariable Long id,Pageable pageable,
                                           @RequestParam(defaultValue = "",name = "name")String authorName){
        return gettingDataAuthorService.getAuthorSubscribers(id,authorName,pageable);
    }

    @GetMapping("/{id}/likes")
    public Page<PoemsDto> getLikes(@PathVariable Long id,Pageable pageable,
                                   @RequestParam(defaultValue = "",name = "poemName")String poemName){
        return gettingDataAuthorService.getAuthorLikes(id,poemName,pageable);
    }


    private URI getUriFromCurrentRequest(){
        return ServletUriComponentsBuilder.fromCurrentRequestUri()
                .build().toUri();
    }
}




