package com.example.internship.service.impl;

import com.example.internship.dto.ProjectDTO;
import com.example.internship.exception.BadRequestException;
import com.example.internship.exception.OptimisticLockConflictException;
import com.example.internship.exception.ResourceNotFoundException;
import com.example.internship.model.Project;
import com.example.internship.repository.ClientRepo;
import com.example.internship.repository.ProjectRepo;
import com.example.internship.repository.TeamMemberRepo;
import com.example.internship.service.ProjectService;
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
public class ProjectServiceImpl implements ProjectService {
    @Autowired
    private Logger log;
    private final ProjectRepo projectRepo;
    private final TeamMemberRepo teamMemberRepo;
    private final ClientRepo clientRepo;

    @Override
    public List<Project> findAll() {
        var projects = projectRepo.findAll();
        if(projects.isEmpty()){
            throw new ResourceNotFoundException("Not found any project in DB");
        }
        log.info("All projects returned");
        return projects;
    }

    @Override
    public Map<String, Object> findPageable(int size, int page) {

            var paging = PageRequest.of(page,size);
            var pageableProjects = projectRepo.findAll(paging);

            if(pageableProjects.isEmpty()){
                throw new ResourceNotFoundException("Not found any project in DB");
            }

            var projects = pageableProjects.getContent();

            Map<String, Object> response = new HashMap<>();
            response.put("projects", projects);
            response.put("currentPage",pageableProjects.getNumber());
            response.put("totalItems", pageableProjects.getTotalElements());
            response.put("totalPages", pageableProjects.getTotalPages());
        log.info("All pageable projects returned");
        return response;
    }

    @Override
    public Project findById(Long id) {
        var project =  projectRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Not found project with id: " + id));
        log.info("Found project with id: " + id);
        return project;
    }

    @Override
    public Project findByName(String name) {
        return projectRepo.findByName(name);
    }

    @Override
    public Project save(ProjectDTO projectDTO) {
        if (projectDTO.getName().length() == 0 || projectDTO.getCustomer() == null || projectDTO.getTeamMember() == null){
            throw new BadRequestException("Some input fields are empty, check it");
        }
        var teamMember = teamMemberRepo.findById(projectDTO.getTeamMember()).orElseThrow(() -> new ResourceNotFoundException("Not found Team Member with id: " + projectDTO.getTeamMember()));
        var client = clientRepo.findById(projectDTO.getCustomer()).orElseThrow(() -> new ResourceNotFoundException("Not found Customer with id: " + projectDTO.getCustomer()));

        var project = new Project();
        BeanUtils.copyProperties(projectDTO, project);
        project.setCustomer(client);
        project.setTeamMember(teamMember);
        log.info("Saved project");
        return projectRepo.save(project);
    }

    @Override
    public Project update(ProjectDTO projectDTO) {
        try {
            if (projectDTO.getName().length() == 0 || projectDTO.getCustomer() == null || projectDTO.getTeamMember() == null || projectDTO.getId() == null){
                throw new BadRequestException("Some input fields are empty, check it");
            }

            var teamMember = teamMemberRepo.findById(projectDTO.getTeamMember()).orElseThrow(() -> new ResourceNotFoundException("Not found Team Member with id: " + projectDTO.getTeamMember()));
            var client = clientRepo.findById(projectDTO.getCustomer()).orElseThrow(() -> new ResourceNotFoundException("Not found Customer with id: " + projectDTO.getCustomer()));

            projectRepo.findById(projectDTO.getId()).orElseThrow(() -> new ResourceNotFoundException("Not found project with id: " + projectDTO.getId()));
            log.info("Found project with id: " + projectDTO.getId());

            var project = new Project();
            BeanUtils.copyProperties(projectDTO, project);
            project.setCustomer(client);
            project.setTeamMember(teamMember);
            log.info("Updated project with id: " + project.getId());
            return projectRepo.save(project);
        }catch (OptimisticLockException e){
            throw new OptimisticLockConflictException("Optimistic Lock Exception");
        }

    }

    @Override
    public void delete(Long id) {
        projectRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Not found project with id: " + id));
        log.info("Found project with id: " + id);
        projectRepo.deleteById(id);
        log.info("Project with id: " + id + " was deleted");
    }
}
