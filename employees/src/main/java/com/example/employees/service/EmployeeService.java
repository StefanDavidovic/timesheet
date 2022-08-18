package com.example.employees.service;

import com.example.employees.model.Employee;
import org.springframework.stereotype.Service;


public interface EmployeeService {

    Employee findByEmail(String email);

    void save(Employee employee);

    Employee update(Employee employee);

    void delete(Long id);

}
