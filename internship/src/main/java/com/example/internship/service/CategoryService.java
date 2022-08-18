package com.example.internship.service;

import com.example.internship.dto.CategoryDTO;
import com.example.internship.model.Category;

import java.util.List;
import java.util.Map;

public interface CategoryService {

    List<Category> findAll();
    Map<String, Object> findPageable(int size, int page);
    Category findById(Long id);
    Category findByName(String name);
    Category save(CategoryDTO categoryDTO);
    Category update(CategoryDTO categoryDTO);
    void delete(Long id);

}
