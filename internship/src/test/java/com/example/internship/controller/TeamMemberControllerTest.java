package com.example.internship.controller;

import com.example.TeamMemberRequest;
import com.example.TeamMemberResponse;
import com.example.TeamMemberServiceGrpc;
import com.example.internship.config.GlobalConfig;
import com.example.internship.dto.TeamMemberDTO;
import com.example.internship.model.Project;
import com.example.internship.model.Role;
import com.example.internship.model.TeamMember;
import com.example.internship.repository.TeamMemberRepo;
import com.example.internship.service.impl.SendMailService;
import com.example.internship.service.impl.TeamMemberServiceImpl;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import org.junit.BeforeClass;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;
import java.util.*;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class TeamMemberControllerTest {
    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper mapper;
    @MockBean
    private RestTemplate restTemplate;
    @MockBean
    TeamMemberRepo teamMemberRepo;
    @Autowired
    private GlobalConfig globalConfig;
    TeamMember teamMember1 = new TeamMember(1l, "pera", 12, "pero@gmail.com", "pera123", true, true, Role.ADMIN,0);
    TeamMember teamMember2 = new TeamMember(2l, "mare", 12, "mare@gmail.com", "mare123", true, true, Role.WORKER,0);

    @BeforeEach
    public void Init() throws Exception {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
    }

    @Test
    public void getMembers_success() throws Exception{
        var members = new ArrayList<>(Arrays.asList(teamMember1,teamMember2));

        Mockito.when(teamMemberRepo.findAll()).thenReturn(members);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/teamMembers").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[1].username").value("mare"));
    }

    @Test
    public void getMembersPageable_success() throws Exception{
        var members = new ArrayList<>(Arrays.asList(teamMember1,teamMember2));
        var teamMemberPage = new PageImpl(members);
        Mockito.when(teamMemberRepo.findAll(org.mockito.ArgumentMatchers.isA(Pageable.class))).thenReturn(teamMemberPage);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/teamMembers/pageable").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(3)));
    }

    @Test
    public void getMemberById_success() throws Exception{
        var localRestTemplate = new TestRestTemplate();
        var responseEntity = localRestTemplate.getForEntity(globalConfig.employeeUrl() +"mare@gmail.com", JsonNode.class);

        Mockito.when(teamMemberRepo.findById(2l)).thenReturn(Optional.ofNullable(teamMember2));
        Mockito.when(restTemplate.getForEntity(Mockito.anyString(), ArgumentMatchers.any(Class.class))).thenReturn(responseEntity);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/teamMembers/2").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.username").value("mare"));
    }

    @Test
    public void createMember_success() throws Exception{
        var teamMember = TeamMember.builder()
                .username("ivke")
                .hoursPerWeek(20)
                .email("mare@gmail.com")
                .password("$2y$10$i9gu7HQ8ORQNLk77Fustq.cpL3rwKEuckQYrMuk2csr.IhpYcQfkq")
                .status(true)
                .archive(true)
                .role(Role.WORKER)
                .build();

        var teamMemberDto = TeamMemberDTO.builder()
                .name("Ivan")
                .username("ivke")
                .hoursPerWeek(20)
                .email("mare@gmail.com")
                .password("ivke123")
                .role("WORKER")
                .status(true)
                .archive(true)
                .build();

        var localRestTemplate = new TestRestTemplate();
        var responseEntity = localRestTemplate.getForEntity(globalConfig.employeeUrl() +"mare@gmail.com", JsonNode.class);

        Mockito.when(teamMemberRepo.save(teamMember)).thenReturn(teamMember);
        Mockito.when(restTemplate.getForEntity(Mockito.anyString(), ArgumentMatchers.any(Class.class))).thenReturn(responseEntity);

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/api/teamMembers")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(teamMemberDto));

        mockMvc.perform(mockRequest)
                .andExpect(status().isCreated());
    }

    @Test
    public void updateMember_success() throws Exception{
        var teamMember = TeamMember.builder()
                .id(2l)
                .username("ivke")
                .hoursPerWeek(20)
                .email("mare@gmail.com")
                .password("ivke123")
                .status(true)
                .archive(true)
                .role(Role.WORKER)
                .build();

        var teamMemberDto = TeamMemberDTO.builder()
                .id(2l)
                .name("Ivan")
                .username("ivke")
                .hoursPerWeek(20)
                .email("mare@gmail.com")
                .password("ivke123")
                .role("WORKER")
                .status(true)
                .archive(true)
                .build();

        Mockito.when(teamMemberRepo.findById(teamMember2.getId())).thenReturn(Optional.ofNullable((teamMember2)));
        Mockito.when(teamMemberRepo.save(teamMember)).thenReturn(teamMember);

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.put("/api/teamMembers/2")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(teamMemberDto));

        mockMvc.perform(mockRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.username").value("ivke"));
    }

    @Test
    public void updateMember_nullId() throws Exception {
        var teamMemberDto = TeamMemberDTO.builder()
                .name("Ivan")
                .username("ivke")
                .hoursPerWeek(20)
                .email("ivke@gmail.com")
                .password("ivke123")
                .role("WORKER")
                .status(true)
                .archive(true)
                .build();

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.put("/api/teamMembers/5")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(teamMemberDto));

        mockMvc.perform(mockRequest)
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateCategory_notFound() throws Exception{
        var teamMemberDto = TeamMemberDTO.builder()
                .name("Ivan")
                .username("ivke")
                .hoursPerWeek(20)
                .email("ivke@gmail.com")
                .password("ivke123")
                .role("WORKER")
                .status(true)
                .archive(true)
                .build();

        Mockito.when(teamMemberRepo.findById(teamMember2.getId())).thenReturn((null));

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.put("/api/teamMembers/5")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(teamMemberDto));

        mockMvc.perform(mockRequest)
                .andExpect(status().isBadRequest());
    }

    @Test
    public void deleteMember_success() throws Exception {
        Mockito.when(teamMemberRepo.findById(teamMember2.getId())).thenReturn(Optional.ofNullable(teamMember2));

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/api/teamMembers/2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void deleteMemberById_notFound() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/api/teamMembers/55")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

    }
}
