package com.example.internship.service.impl;

import com.example.internship.dto.ClientDTO;
import com.example.internship.exception.BadRequestException;
import com.example.internship.exception.OptimisticLockConflictException;
import com.example.internship.exception.ResourceNotFoundException;
import com.example.internship.model.Client;
import com.example.internship.repository.ClientRepo;
import com.example.internship.service.ClientService;
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
public class ClientServiceImpl implements ClientService {
    @Autowired
    private Logger log;
    private final ClientRepo clientRepo;

    @Override
    public List<Client> findAll() {
        var clients = clientRepo.findAll();
        if(clients.isEmpty()){
            throw new ResourceNotFoundException("Not found any client in DB");
        }
        log.info("All clients returned");
        return clients;
    }

    @Override
    public Map<String, Object> findPageable(int size, int page) {

            var paging = PageRequest.of(page,size);

            var pageableClients = clientRepo.findAll(paging);

            if(pageableClients.isEmpty()){
                throw new ResourceNotFoundException("Not found any client in DB");
            }
            var clients = pageableClients.getContent();

            Map<String, Object> response = new HashMap<>();
            response.put("clients", clients);
            response.put("currentPage",pageableClients.getNumber());
            response.put("totalItems", pageableClients.getTotalElements());
            response.put("totalPages", pageableClients.getTotalPages());
        log.info("All pageable clients returned");
        return response;
    }

    @Override
    public Client findById(Long id) {
        var client =  clientRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Not found client with id: " + id));
        log.info("Found client with id: " + id);
        return client;
    }

    @Override
    public Client findByName(String name) {
        return clientRepo.findByName(name);
    }

    @Override
    public Client save(ClientDTO clientDTO) {
        if(clientDTO.getName().length() == 0 || clientDTO.getCountry().length() == 0 || clientDTO.getZip().length() == 0){
            throw new BadRequestException("Input fields are empty");
        }
        var client = new Client();
        BeanUtils.copyProperties(clientDTO, client);
        log.info("Saved client");
        return clientRepo.save(client);
    }

    @Override
    public Client update(ClientDTO clientDTO) {
        try{
            if(clientDTO.getName().length() == 0 || clientDTO.getCountry().length() == 0 || clientDTO.getZip().length() == 0 || clientDTO.getId() == null){
                throw new BadRequestException("Input fields are empty");
            }
            clientRepo.findById(clientDTO.getId()).orElseThrow(() -> new ResourceNotFoundException("Not found client with id: " + clientDTO.getId()));
            log.info("Found client with id: " + clientDTO.getId());
            var client = new Client();
            BeanUtils.copyProperties(clientDTO, client);

            return clientRepo.save(client);
        }catch (OptimisticLockException e){
            throw new OptimisticLockConflictException("Optimistic lock conflict");
        }
    }

    @Override
    public void delete(Long id) {
        clientRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Not found client with id: " + id));
        log.info("Found client with id: " + id);
        clientRepo.deleteById(id);
        log.info("Client with id: " + id + "was deleted");
    }
}
