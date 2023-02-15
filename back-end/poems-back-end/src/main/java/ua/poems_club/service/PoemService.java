package ua.poems_club.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ua.poems_club.dto.poem.PoemDto;
import ua.poems_club.dto.poem.PoemsDto;

public interface PoemService {
    Page<PoemsDto> getAllPoems(Pageable pageable,String name);
    PoemDto getPoemById(Long id);
}
