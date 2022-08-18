package com.example.internship.controller;

import com.example.internship.dto.TimeSheetDTO;
import com.example.internship.model.*;
import com.example.internship.repository.TimeSheetRepo;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;
import java.time.LocalDate;
import java.util.*;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class TimeSheetControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper mapper;
    @MockBean
    private RestTemplate restTemplate;
    @MockBean
    TimeSheetRepo timeSheetRepo;

    TeamMember teamMember = new TeamMember(1l, "member", 14, "member@gmail.com", "pass", true, true, Role.ADMIN,0);
    Client client = new Client(1l, "client", "address", "Novi Sad", "12020", "Srbija",0);
    Project project = new Project(1l, "project", "some proj", true, true, client, teamMember,0);
    Category category = new Category(1l, "categoryName",0);

    TimeSheet timeSheet1 = new TimeSheet(1l, LocalDate.parse("2021-12-12"), "Some desc", Float.parseFloat("15.5"), Float.parseFloat("1.5"), client, project, category,0);
    TimeSheet timeSheet2 = new TimeSheet(2l, LocalDate.parse("2021-12-12"), "Some other desc", Float.parseFloat("17.0"), Float.parseFloat("2.5"), client, project, category,0);

    @BeforeEach
    public void Init() throws Exception {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
    }

    @Test
    public void getSheets_success() throws Exception{
        var sheets = new ArrayList<>(Arrays.asList(timeSheet1, timeSheet2));
        Mockito.when(timeSheetRepo.findAll()).thenReturn(sheets);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/timeSheets?id=1&date=2021-12-12").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[1].description").value("Some other desc"));
    }

    @Test
    public void getSheetById_success() throws Exception{
        Mockito.when(timeSheetRepo.findById(2l)).thenReturn(Optional.ofNullable(timeSheet2));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/timeSheets/2").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.description").value("Some other desc"));
    }

    @Test
    public void createSheet_success() throws Exception{
        var timeSheet = TimeSheet.builder()
                        .date(LocalDate.parse("2020-12-12"))
                        .description("Some desc2")
                        .time(Float.parseFloat("17.0"))
                        .overtime(Float.parseFloat("1.0"))
                        .build();


        var sheetDTO = TimeSheetDTO.builder()
                .date(LocalDate.parse("2020-12-12"))
                .description("Some desc2")
                .time(Float.parseFloat("17.0"))
                .overtime(Float.parseFloat("1.0"))
                .client(1l)
                .project(6l)
                .category(3l)
                .build();

        Mockito.when(timeSheetRepo.save(timeSheet)).thenReturn(timeSheet);

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/api/timeSheets")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(sheetDTO));

        mockMvc.perform(mockRequest)
                .andExpect(status().isCreated());
    }

    @Test
    public void updateSheet_success() throws Exception{
        var timeSheet = TimeSheet.builder()
                .id(2l)
                .date(LocalDate.parse("2020-12-12"))
                .description("Some desc2")
                .time(Float.parseFloat("17.0"))
                .overtime(Float.parseFloat("1.0"))
                .build();


        var sheetDTO = TimeSheetDTO.builder()
                .id(2l)
                .date(LocalDate.parse("2020-12-12"))
                .description("Some desc2")
                .time(Float.parseFloat("17.0"))
                .overtime(Float.parseFloat("1.0"))
                .client(1l)
                .project(1l)
                .category(1l)
                .build();

        Mockito.when(timeSheetRepo.findById(timeSheet2.getId())).thenReturn(Optional.ofNullable((timeSheet2)));
        Mockito.when(timeSheetRepo.save(timeSheet)).thenReturn(timeSheet);

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.put("/api/timeSheets/2")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(sheetDTO));

        mockMvc.perform(mockRequest)
                .andExpect(status().isOk());
    }

    @Test
    public void updateSheet_nullId() throws Exception {
        var sheetDTO = TimeSheetDTO.builder()
                .date(LocalDate.parse("2020-12-12"))
                .description("Some desc2")
                .time(Float.parseFloat("17.0"))
                .overtime(Float.parseFloat("1.0"))
                .client(1l)
                .project(1l)
                .category(1l)
                .build();

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.put("/api/timeSheets/5")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(sheetDTO));

        mockMvc.perform(mockRequest)
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateSheet_notFound() throws Exception{
        var sheetDTO = TimeSheetDTO.builder()
                .date(LocalDate.parse("2020-12-12"))
                .description("Some desc2")
                .time(Float.parseFloat("17.0"))
                .overtime(Float.parseFloat("1.0"))
                .client(1l)
                .project(1l)
                .category(1l)
                .build();

        Mockito.when(timeSheetRepo.findById(timeSheet2.getId())).thenReturn((null));

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.put("/api/timeSheets/5")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(sheetDTO));

        mockMvc.perform(mockRequest)
                .andExpect(status().isBadRequest());
    }

    @Test
    public void deleteSheet_success() throws Exception {
        Mockito.when(timeSheetRepo.findById(timeSheet2.getId())).thenReturn(Optional.ofNullable(timeSheet2));

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/api/timeSheets/2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void deleteMemberById_notFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/api/timeSheets/45")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
