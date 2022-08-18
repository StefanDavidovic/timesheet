package com.example.internship.service;

import com.example.internship.dto.ProjectDTO;
import com.example.internship.model.Project;
import java.util.List;
import java.util.Map;

public interface ProjectService {

    List<Project> findAll();
    Map<String, Object> findPageable(int size, int page);
    Project findById(Long id);
    Project findByName(String name);
    Project save(ProjectDTO projectDTO);
    Project update(ProjectDTO projectDTO);
    void delete(Long id);

}
