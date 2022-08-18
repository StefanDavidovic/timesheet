package com.example.employees.service.impl;

import com.example.employees.model.Employee;
import com.example.employees.repository.EmployeeRepo;
import com.example.employees.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeRepo employeeRepo;

    @Override
    public Employee findByEmail(String email) {
        return employeeRepo.findByEmail(email);
    }

    @Override
    public void save(Employee employee) {
        employeeRepo.save(employee);
    }

    @Override
    public Employee update(Employee employee) {
        return employeeRepo.save(employee);
    }

    @Override
    public void delete(Long id) {
        employeeRepo.deleteById(id);
    }
}
