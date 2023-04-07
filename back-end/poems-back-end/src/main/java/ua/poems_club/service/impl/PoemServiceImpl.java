package ua.poems_club.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
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
    @Cacheable(value = "poems", key = "#currentUserId+'_'+#name+'_'+pageable.pageNumber")
    public Page<PoemsDto> getAllPoems(Long currentUserId, Pageable pageable, String name) {
        return getPoems(currentUserId,pageable,name);
    }

    private Page<PoemsDto> getPoems(Long currentUserId, Pageable pageable, String name){
        return name.isEmpty()? getAll(currentUserId,pageable) : getAllByName(currentUserId,pageable, name);
    }

    private Page<PoemsDto> getAll(Long currentUserId, Pageable pageable){
        var poems = poemRepository.findAllPoems(currentUserId,pageable);
        checkIsPoems(poems.getContent());
        return poems;
    }

    private void checkIsPoems(List<PoemsDto> poems){
        if (poems.isEmpty()){
            throw new NotFoundException("Cannot find any poems");
        }
    }

    private Page<PoemsDto> getAllByName(Long currentUserId, Pageable pageable, String name) {
        var poems = poemRepository.findAllPoemsWhichContainText(currentUserId,pageable, name);
        checkIsPoems(poems.getContent());
        return poems;
    }

    @Override
    @Cacheable("poem")
    public PoemDto getPoemById(Long id) {
        return getById(id);
    }

    private PoemDto getById(Long id){
        return poemRepository.findPoemById(id)
                .orElseThrow(()->new NotFoundException("Cannot find poem by id: "+id));
    }

}

