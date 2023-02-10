package ua.poems_club.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.poems_club.model.Poem;
import ua.poems_club.repository.PoemRepository;

@RestController
@RequestMapping("api/poems")
@RequiredArgsConstructor
public class PoemController {
    private final PoemRepository poemRepository;

    @GetMapping
    public Page<Poem> getAll(Pageable pageable){
        return poemRepository.findAll(pageable);
    }

}
