package com.teachsync.controllers.domain;

import com.teachsync.dto_s.categories.CategoryBaseDto;
import com.teachsync.dto_s.categories.CategoryCreateDto;
import com.teachsync.services.domain.CategoryService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/teachsync/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<CategoryBaseDto>> getAll(){
        return ResponseEntity.ok(categoryService.getAll());
    }

    @PostMapping("/create")
    public ResponseEntity<Void> create(@RequestBody @Valid CategoryCreateDto createDto){
        categoryService.createCategory(createDto);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Void> create(@PathVariable Long id, @Valid CategoryCreateDto createDto){
        categoryService.update(id, createDto);
        return ResponseEntity.ok().build();
    }
}
