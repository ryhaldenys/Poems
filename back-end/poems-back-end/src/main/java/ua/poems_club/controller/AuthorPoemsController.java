package ua.poems_club.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ua.poems_club.dto.poem.RequestPoemDto;
import ua.poems_club.dto.poem.PoemsDto;
import ua.poems_club.security.model.SecurityUser;
import ua.poems_club.service.AuthorPoemService;

import static org.springframework.http.HttpStatus.*;


@RestController
@RequestMapping("/api/authors/{id}/poems")
@RequiredArgsConstructor
public class AuthorPoemsController {
    private final AuthorPoemService authorPoemService;

    @GetMapping
    public Page<PoemsDto> getAllPoemsOfAuthor(@PathVariable Long id, Pageable pageable,@AuthenticationPrincipal SecurityUser currentUser,
                                              @RequestParam(defaultValue = "",name = "poemName")String poemName){
        return authorPoemService.getAllPublicPoemsByAuthorIdAndContainText(id,currentUser.getId(),poemName,pageable);
    }

    @GetMapping("/own")
    @PreAuthorize("authentication.principal.id == #id")
    public Page<PoemsDto> getAllPoemsOfCurrentAuthor(@PathVariable Long id, Pageable pageable){
        return authorPoemService.getAllPoemsByAuthorId(id,id,pageable);
    }

    @PostMapping
    @PreAuthorize("authentication.principal.id == #id")
    @ResponseStatus(NO_CONTENT)
    public void createPoem(@PathVariable Long id,@RequestBody RequestPoemDto poem){
        authorPoemService.createPoem(id,poem);
    }

    @PutMapping(value = "/{poem_id}")
    @PreAuthorize("authentication.principal.id == #id")
    @ResponseStatus(NO_CONTENT)
    public void updatePoem(@PathVariable Long id, @PathVariable("poem_id") Long poemId,@RequestBody RequestPoemDto poem){
        authorPoemService.updatePoem(id,poemId,poem);
    }

    @DeleteMapping("/{poem_id}")
    @PreAuthorize("authentication.principal.id == #id")
    @ResponseStatus(NO_CONTENT)
    public void deletePoem(@PathVariable Long id,@PathVariable("poem_id") Long poemId){
        authorPoemService.deletePoem(id,poemId);
    }

    @PatchMapping("/{poem_id}/likes")
    @PreAuthorize("authentication.principal.id == #id")
    @ResponseStatus(NO_CONTENT)
    public void updateLikes(@PathVariable Long id, @PathVariable("poem_id") Long poemId){
        authorPoemService.updatePoemLikes(id,poemId);
    }
}
