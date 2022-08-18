package com.example.internship.controller;

import com.example.internship.model.Category;
import com.example.internship.model.TimeSheet;
import com.example.internship.repository.CategoryRepo;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import java.util.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class CategoryControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper mapper;
    @MockBean
    CategoryRepo categoryRepo;

    Set<TimeSheet> sheets = new HashSet<>();
    Category category1 = new Category(1l, "CategoryTest1",0);
    Category category2 = new Category(2l, "CategoryTest2",0);

    @BeforeEach
    public void Init() throws Exception {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
    }

    @Test
    public void getCategories_success() throws Exception{
        var categories = new ArrayList<>(Arrays.asList(category1,category2));

        Mockito.when(categoryRepo.findAll()).thenReturn(categories);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/categories").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[1].name").value("CategoryTest2"));
    }

    @Test
    public void getCategoriesPageable_success() throws Exception{
        var categories = new ArrayList<>(Arrays.asList(category1,category2));
        var categoryPage = new PageImpl(categories);
        Mockito.when(categoryRepo.findAll(org.mockito.ArgumentMatchers.isA(Pageable.class))).thenReturn(categoryPage);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/categories/pageable").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(3)));
    }

    @Test
    public void getCategoryById_success() throws Exception{
        Mockito.when(categoryRepo.findById(2l)).thenReturn(Optional.ofNullable(category2));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/categories/2").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.name").value("CategoryTest2"));

    }

    @Test
    public void createCategory_success() throws Exception{
        var category = Category.builder()
                .name("Some category")
                .build();

        Mockito.when(categoryRepo.save(category)).thenReturn(category);

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/api/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(category));

        mockMvc.perform(mockRequest)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.name").value("Some category"));
    }

    @Test
    public void updateCategory_success() throws Exception{
        var updatedCategory = Category.builder()
                .id(2l)
                .name("CategoryTest1")
                .build();

        Mockito.when(categoryRepo.findById(category2.getId())).thenReturn(Optional.ofNullable((category2)));
        Mockito.when(categoryRepo.save(updatedCategory)).thenReturn(updatedCategory);

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.put("/api/categories/2")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(updatedCategory));

        mockMvc.perform(mockRequest)
                .andExpect(status().isOk());
    }

    @Test
    public void updateCategory_nullId() throws Exception {
        var updatedCategory = Category.builder()
                .name("CategoryTest5")
                .build();

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.put("/api/categories/5")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(updatedCategory));

        mockMvc.perform(mockRequest)
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateCategory_notFound() throws Exception{
        var updatedCategory = Category.builder()
                .id(5l)
                .name("CategoryTest5")
                .build();

        Mockito.when(categoryRepo.findById(category2.getId())).thenReturn((null));

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.put("/api/categories/5")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(updatedCategory));

        mockMvc.perform(mockRequest)
                .andExpect(status().isNotFound());

    }

    @Test
    public void deleteCategory_success() throws Exception {
        Mockito.when(categoryRepo.findById(category2.getId())).thenReturn(Optional.ofNullable(category2));

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/api/categories/2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void deleteCategoryById_notFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/api/categories/5")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

    }
}
