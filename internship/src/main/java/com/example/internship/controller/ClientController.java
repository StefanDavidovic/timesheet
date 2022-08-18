package com.example.internship.controller;

import com.example.internship.dto.ClientDTO;
import com.example.internship.model.Client;
import com.example.internship.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;

    @GetMapping()
    public ResponseEntity<List<Client>> getClients(){
            return new ResponseEntity<>(clientService.findAll(), HttpStatus.OK);
    }

    @GetMapping("/pageable")
    public ResponseEntity<Map<String, Object>> getAllClients(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "3") int size) {
            return new ResponseEntity<>(clientService.findPageable(size,page), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Client> getClient(@PathVariable(value="id") Long id){
        return new ResponseEntity<>(clientService.findById(id), HttpStatus.OK);
    }

    @PostMapping()
    public ResponseEntity<Client> saveClient(@RequestBody ClientDTO clientDTO){
            return new ResponseEntity(clientService.save(clientDTO),HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Client> updateClient(@RequestBody ClientDTO clientDTO){
                return new ResponseEntity<>(clientService.update(clientDTO),  HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteClient(@PathVariable(value="id") Long id) {
        clientService.delete(id);
        return new ResponseEntity<>("Client with id " + id + "was successfully deleted", HttpStatus.OK);
    }
}
