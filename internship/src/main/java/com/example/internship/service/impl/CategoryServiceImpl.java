package com.example.internship.service.impl;

import com.example.internship.dto.CategoryDTO;
import com.example.internship.exception.BadRequestException;
import com.example.internship.exception.OptimisticLockConflictException;
import com.example.internship.exception.ResourceNotFoundException;
import com.example.internship.model.Category;
import com.example.internship.repository.CategoryRepo;
import com.example.internship.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import javax.persistence.OptimisticLockException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private Logger log;
    private final CategoryRepo categoryRepo;

    @Override
    public List<Category> findAll() {
        var categories = categoryRepo.findAll();
        if(categories.isEmpty()){
            throw new ResourceNotFoundException("Not found any category in DB");
        }
        log.info("All categories returned");
        return categories;
    }

    @Override
    public Map<String, Object> findPageable(int size, int page) {
            var paging = PageRequest.of(page,size);
            var pageableCategories = categoryRepo.findAll(paging);

            if(pageableCategories.isEmpty()){
                throw new ResourceNotFoundException("Not found any category in DB");
            }
            var categories = pageableCategories.getContent();

            Map<String, Object> response = new HashMap<>();
            response.put("categories", categories);
            response.put("currentPage",pageableCategories.getNumber());
            response.put("totalItems", pageableCategories.getTotalElements());
        log.info("All pageable categories returned");
        return response;
    }

    @Override
    public Category findById(Long id) {
        var category = categoryRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Not found category with id: " + id));
        log.info("Found Category with id" + id);
        return category;
    }

    @Override
    public Category findByName(String name) {
        return categoryRepo.findByName(name);
    }

    @Override
    public Category save(CategoryDTO categoryDTO) {
        if(categoryDTO.getName().isEmpty() || categoryDTO.getName().length() == 0){
            throw new BadRequestException("Input fields are empty");
        }
        var category = new Category();
        BeanUtils.copyProperties(categoryDTO, category);

        log.info("Saved category");
        return categoryRepo.save(category);
    }

    @Override
    public Category update(CategoryDTO categoryDTO){
        try{
            if(categoryDTO.getName().isEmpty() || categoryDTO.getName().length() == 0 || categoryDTO.getId() == null){
                throw new BadRequestException("Input fields are empty");
            }
            var category = categoryRepo.findById(categoryDTO.getId()).orElseThrow(() -> new ResourceNotFoundException("Not found category with id: " + categoryDTO.getId()));
            log.info("Found category with id: " + categoryDTO.getId());

            BeanUtils.copyProperties(categoryDTO, category);
            var categoryUpdated = categoryRepo.save(category);
            return categoryUpdated;
        }catch (OptimisticLockException e){
            throw new OptimisticLockConflictException("Optimistic lock conflict");
        }
    }

    @Override
    public void delete(Long id) {
            categoryRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Not found category with id: " +id));
            log.info("Found category with id: " + id);
            categoryRepo.deleteById(id);
            log.info("Category with id: " + id + "was deleted");
    }
}
