package com.teachsync.services.domain;

import com.teachsync.domain.Category;
import com.teachsync.dto_s.categories.CategoryBaseDto;
import com.teachsync.dto_s.categories.CategoryCreateDto;
import com.teachsync.mappers.categories.CategoryMapper;
import com.teachsync.repositories.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<CategoryBaseDto> getAll(){
        return categoryRepository.findAll().stream().map(CategoryMapper::mapToDto).collect(Collectors.toList());
    }

    @Transactional
    public void createCategory(CategoryCreateDto createDto){
        Category category = CategoryMapper.mapToEntity(createDto);
        categoryRepository.save(category);
    }

    @Transactional
    public void update(Long catId, CategoryCreateDto categoryCreateDto){
        Category category = categoryRepository.findById(catId)
                .orElseThrow(() -> new NoSuchElementException("category not found"));
        if(StringUtils.hasText(categoryCreateDto.getName())){
            category.setName(categoryCreateDto.getName());
        }
    }
}
