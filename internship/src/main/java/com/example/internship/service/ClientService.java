package com.example.internship.service;

import com.example.internship.dto.ClientDTO;
import com.example.internship.model.Client;
import java.util.List;
import java.util.Map;

public interface ClientService {

    List<Client> findAll();
    Map<String, Object> findPageable(int size, int page);
    Client findById(Long id);
    Client findByName(String name);
    Client save(ClientDTO clientDTO);
    Client update(ClientDTO clientDTO);
    void delete(Long id);

}
