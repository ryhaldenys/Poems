package ua.poems_club.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ua.poems_club.dto.poem.PoemDto;
import ua.poems_club.dto.poem.PoemsDto;
import ua.poems_club.security.model.SecurityUser;
import ua.poems_club.service.PoemService;

@RestController
@RequestMapping("/api/poems")
@RequiredArgsConstructor
public class PoemController {
    private final PoemService poemService;

    @GetMapping
    public Page<PoemsDto> getAll(@AuthenticationPrincipal SecurityUser author, Pageable pageable,
                                 @RequestParam(defaultValue = "")String name){
        return poemService.getAllPoems(author.getId(),pageable,name);
    }

    @GetMapping("/{id}")
    public PoemDto getById(@PathVariable Long id){
        return poemService.getPoemById(id);
    }
}
