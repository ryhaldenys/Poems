package ua.poems_club.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.poems_club.dto.poem.PoemDto;
import ua.poems_club.dto.poem.PoemsDto;
import ua.poems_club.exception.NotFoundException;
import ua.poems_club.repository.PoemRepository;
import ua.poems_club.service.PoemService;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PoemServiceImpl implements PoemService {
    private final PoemRepository poemRepository;

    @Override
    public Page<PoemsDto> getAllPoems(Pageable pageable,String name) {
        return getPoems(pageable,name);
    }

    private Page<PoemsDto> getPoems(Pageable pageable,String name){
        return name.isEmpty()? getAll(pageable) : getAllByName(pageable, name);
    }

    private Page<PoemsDto> getAll(Pageable pageable){
        var poems = poemRepository.findAllPoems(pageable);
        checkIsPoems(poems.getContent());
        return poems;
    }

    private void checkIsPoems(List<PoemsDto> poems){
        if (poems.isEmpty()){
            throw new NotFoundException("Cannot find any poems");
        }
    }

    private Page<PoemsDto> getAllByName(Pageable pageable, String name) {
        var poems = poemRepository.findAllPoemsByName(pageable, name);
        checkIsPoems(poems.getContent());
        return poems;
    }

    @Override
    public PoemDto getPoemById(Long id) {
        return getById(id);
    }

    private PoemDto getById(Long id){
        return poemRepository.findPoemById(id)
                .orElseThrow(()->new NotFoundException("Cannot find poem by id: "+id));
    }

}

