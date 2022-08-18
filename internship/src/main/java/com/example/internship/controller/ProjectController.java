package com.example.internship.controller;

import com.example.internship.dto.ProjectDTO;
import com.example.internship.model.Project;
import com.example.internship.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {
    private final ProjectService projectService;

    @GetMapping()
    public ResponseEntity<List<Project>> getProjects(){
        return new ResponseEntity<>(projectService.findAll(), HttpStatus.OK);
    }

    @GetMapping("/pageable")
    public ResponseEntity<Map<String, Object>> getAllProjects(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "3") int size) {
            return new ResponseEntity<>(projectService.findPageable(size,page), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Project> getProjects(@PathVariable(value="id") Long id){
        return new ResponseEntity<>(projectService.findById(id), HttpStatus.OK);
    }

    @PostMapping()
    public ResponseEntity<Project> saveProject(@RequestBody ProjectDTO projectDTO){
        return new ResponseEntity(projectService.save(projectDTO),HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Project> updateProject(@RequestBody ProjectDTO projectDTO){
        return new ResponseEntity<>(projectService.update(projectDTO),  HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProject(@PathVariable(value="id") Long id) {
        projectService.delete(id);
        return new ResponseEntity<>("Project with id " + id + " was successfully deleted", HttpStatus.OK);
    }
}
