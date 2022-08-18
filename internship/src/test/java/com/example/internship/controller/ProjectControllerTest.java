package com.example.internship.controller;


import com.example.internship.dto.ProjectDTO;
import com.example.internship.model.*;
import com.example.internship.repository.ProjectRepo;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.*;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class ProjectControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper mapper;

    @MockBean
    ProjectRepo projectRepo;

    TeamMember member = new TeamMember(1l, "pera", 12, "pera@gmail.com", "pera123", true, true, Role.ADMIN,0);
    Client client = new Client(1l, "Client2", "Address2", "City2", "14200", "Serbia",0);

    Project project1 = new Project(1l, "First project", "Web developmnet", true, true, client, member,0);
    Project project2 = new Project(2l, "Second project", "Web developmnet", true, true, client, member,0);

    @BeforeEach
    public void Init() throws Exception {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
    }

    @Test
    public void getProjects_success() throws Exception{
        var projects = new ArrayList<>(Arrays.asList(project1,project2));

        Mockito.when(projectRepo.findAll()).thenReturn(projects);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/projects").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[1].name").value("Second project"));
    }

    @Test
    public void getProjectsPageable_success() throws Exception{
        var projects = new ArrayList<>(Arrays.asList(project1,project2));
        var projectPage = new PageImpl(projects);
        Mockito.when(projectRepo.findAll(org.mockito.ArgumentMatchers.isA(Pageable.class))).thenReturn(projectPage);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/projects/pageable").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(4)));
    }

    @Test
    public void getProjectById_success() throws Exception{
        Mockito.when(projectRepo.findById(2l)).thenReturn(Optional.ofNullable(project2));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/projects/2").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.name").value("Second project"));

    }

    @Test
    public void createProject_success() throws Exception{
        var project = Project.builder()
                .name("Some project")
                .description("Some description")
                .status(true)
                .archive(true)
                .customer(client)
                .teamMember(member)
                .build();

        var projectDTO = ProjectDTO.builder()
                .name("Some project")
                .description("Some description")
                .status(true)
                .archive(true)
                .customer(1l)
                .teamMember(7l)
                .build();

        Mockito.when(projectRepo.save(project)).thenReturn(project);

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/api/projects")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(projectDTO));

        mockMvc.perform(mockRequest)
                .andExpect(status().isCreated());
    }

    @Test
    public void updateProject_success() throws Exception{
        var project = Project.builder()
                .id(2l)
                .name("Some project")
                .description("Some description")
                .status(true)
                .archive(true)
                .customer(client)
                .teamMember(member)
                .build();

        var projectDTO = ProjectDTO.builder()
                .id(2l)
                .name("Some project")
                .description("Some description")
                .status(true)
                .archive(true)
                .customer(1l)
                .teamMember(7l)
                .build();

        Mockito.when(projectRepo.findById(project2.getId())).thenReturn(Optional.ofNullable((project2)));
        Mockito.when(projectRepo.save(project)).thenReturn(project);

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.put("/api/projects/2")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(projectDTO));

        mockMvc.perform(mockRequest)
                .andExpect(status().isOk());
    }


    @Test
    public void updateProject_nullId() throws Exception {
        var projectDTO = ProjectDTO.builder()
                .name("Some project")
                .description("Some description")
                .status(true)
                .archive(true)
                .customer(1l)
                .teamMember(1l)
                .build();

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.put("/api/projects/5")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(projectDTO));

        mockMvc.perform(mockRequest)
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateProject_notFound() throws Exception{
        var projectDTO = ProjectDTO.builder()
                .id(5l)
                .name("Some project")
                .description("Some description")
                .status(true)
                .archive(true)
                .customer(1l)
                .teamMember(1l)
                .build();

        Mockito.when(projectRepo.findById(project2.getId())).thenReturn((null));

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.put("/api/projects/5")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(projectDTO));

        mockMvc.perform(mockRequest)
                .andExpect(status().isNotFound());
    }

    @Test
    public void deleteProject_success() throws Exception {
        Mockito.when(projectRepo.findById(project2.getId())).thenReturn(Optional.ofNullable(project2));

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/api/projects/2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void deleteProjectById_notFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/api/projects/54")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

    }
}
