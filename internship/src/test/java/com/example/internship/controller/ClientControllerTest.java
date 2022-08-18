package com.example.internship.controller;

import com.example.internship.model.Client;
import com.example.internship.repository.ClientRepo;
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
public class ClientControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper mapper;

    @MockBean
    ClientRepo clientRepo;

    Client client1 = new Client(1l, "Client1", "Address1", "City1", "13200", "Serbia",0);
    Client client2 = new Client(2l, "Client2", "Address2", "City2", "14200", "Serbia",0);


    @BeforeEach
    public void Init() throws Exception {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
    }

    @Test
    public void getClients_success() throws Exception{
        var clients = new ArrayList<>(Arrays.asList(client1,client2));

        Mockito.when(clientRepo.findAll()).thenReturn(clients);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/clients").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[1].name").value("Client2"));
    }

    @Test
    public void getClientsPageable_success() throws Exception{
        var clients = new ArrayList<>(Arrays.asList(client1,client2));
        var clientPage = new PageImpl(clients);
        Mockito.when(clientRepo.findAll(org.mockito.ArgumentMatchers.isA(Pageable.class))).thenReturn(clientPage);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/clients/pageable").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(4)));
    }

    @Test
    public void getClientById_success() throws Exception{
        Mockito.when(clientRepo.findById(2l)).thenReturn(Optional.ofNullable(client2));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/clients/2").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.name").value("Client2"));

    }

    @Test
    public void createClient_success() throws Exception{
        var client = Client.builder()
                .name("Some client")
                .address("Some address")
                .city("Some city")
                .zip("12300")
                .country("Serbia")
                .build();

        Mockito.when(clientRepo.save(client)).thenReturn(client);

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/api/clients")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(client));

        mockMvc.perform(mockRequest)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.name").value("Some client"));
    }

    @Test
    public void updateClient_success() throws Exception{
        var updatedClient = Client.builder()
                .id(2l)
                .name("Some client")
                .address("Some address")
                .city("Some city")
                .zip("12300")
                .country("Serbia")
                .build();

        Mockito.when(clientRepo.findById(client2.getId())).thenReturn(Optional.ofNullable((client2)));
        Mockito.when(clientRepo.save(updatedClient)).thenReturn(updatedClient);

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.put("/api/clients/2")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(updatedClient));

        mockMvc.perform(mockRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.name").value("Some client"));
    }

    @Test
    public void updateCategory_nullId() throws Exception {
        var updatedClient = Client.builder()
                .name("Some client")
                .address("Some address")
                .city("Some city")
                .zip("12300")
                .country("Serbia")
                .build();

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.put("/api/categories/5")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(updatedClient));

        mockMvc.perform(mockRequest)
                .andExpect(status().isBadRequest());
    }


    @Test
    public void updateCategory_notFound() throws Exception{
        var updatedClient = Client.builder()
                .id(5l)
                .name("Some client")
                .address("Some address")
                .city("Some city")
                .zip("12300")
                .country("Serbia")
                .build();

        Mockito.when(clientRepo.findById(client2.getId())).thenReturn((null));

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.put("/api/categories/5")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(updatedClient));

        mockMvc.perform(mockRequest)
                .andExpect(status().isNotFound());

    }

    @Test
    public void deleteCategory_success() throws Exception {
        Mockito.when(clientRepo.findById(client2.getId())).thenReturn(Optional.ofNullable(client2));

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/api/clients/2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void deleteCategoryById_notFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/api/clients/45")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

    }


}
