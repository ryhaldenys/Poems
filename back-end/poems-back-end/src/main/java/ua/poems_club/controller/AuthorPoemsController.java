package ua.poems_club.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ua.poems_club.dto.poem.PoemsDto;
import ua.poems_club.security.model.SecurityUser;
import ua.poems_club.service.AuthorPoemService;



@RestController
@RequestMapping("/api/authors/{id}/poems")
@RequiredArgsConstructor
public class AuthorPoemsController {
    private final AuthorPoemService poemService;

    @GetMapping
    public Page<PoemsDto> getAllPoemsOfAuthor(@PathVariable Long id, Pageable pageable,@AuthenticationPrincipal SecurityUser currentUser){
        return poemService.getAllByAuthorId(id,currentUser.getId(),pageable);
    }

    @GetMapping("/own")
    @PreAuthorize("authentication.principal.id == #id")
    public Page<PoemsDto> getAllPoemsOfCurrentAuthor(@PathVariable Long id, Pageable pageable){
        return poemService.getAllByAuthorId(id,id,pageable);
    }

    @PostMapping
    public ResponseEntity<?> createPoem(@PathVariable Long id){
        throw new RuntimeException();
    }

    @PutMapping("/{poem_id}")
    public ResponseEntity<?> updatePoem(@PathVariable Long id, @PathVariable("poem_id") Long poemId){
        throw new RuntimeException();
    }

    @DeleteMapping("/poem_id")
    public ResponseEntity<?> deletePoem(@PathVariable Long id,@PathVariable("poem_id") Long poemId){
        throw new RuntimeException();
    }
}
