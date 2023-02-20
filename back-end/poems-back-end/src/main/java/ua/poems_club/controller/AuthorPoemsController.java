package ua.poems_club.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.poems_club.dto.poem.PoemsDto;
import ua.poems_club.service.PoemService;

import java.util.List;

@RestController
@RequestMapping("/api/authors/{id}/poems")
@RequiredArgsConstructor
public class AuthorPoemsController {
    private final PoemService poemService;

    @GetMapping
    public List<PoemsDto> getAllPoemsOfAuthor(@PathVariable Long id){
        throw new RuntimeException();
    }

    @GetMapping("/own")
    public List<PoemsDto> getAllPoemsOfCurrentAuthor(@PathVariable Long id){
        throw new RuntimeException();
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
