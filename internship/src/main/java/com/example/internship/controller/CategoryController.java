package com.example.internship.controller;

import com.example.internship.dto.CategoryDTO;
import com.example.internship.model.Category;
import com.example.internship.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<Category>> getCategories(){
        return new ResponseEntity<>(categoryService.findAll(), HttpStatus.OK);
    }

    @GetMapping("/pageable")
    public ResponseEntity<Map<String, Object>> getAllCategories(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "3") int size) {
        return new ResponseEntity<>(categoryService.findPageable(size,page), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategory(@PathVariable(value="id") Long id){
        return new ResponseEntity<>(categoryService.findById(id), HttpStatus.OK);
    }

    @PostMapping()
    public ResponseEntity<Category> saveCategory(@RequestBody CategoryDTO categoryDTO){
        return new ResponseEntity(categoryService.save(categoryDTO),HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Category> updateCategory(@RequestBody CategoryDTO categoryDTO){
        return new ResponseEntity<>(categoryService.update(categoryDTO),  HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCategory(@PathVariable(value="id") Long id){
            categoryService.delete(id);
            return new ResponseEntity<>("Category with id " + id + " was successfully deleted", HttpStatus.OK );
    }

}
