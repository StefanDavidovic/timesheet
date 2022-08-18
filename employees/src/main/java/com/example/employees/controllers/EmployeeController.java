package com.example.employees.controllers;

import com.example.employees.model.Employee;
import com.example.employees.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @GetMapping("/{email}")
    public ResponseEntity<Employee> getEmployee(@PathVariable(value="email") String email){
        try{
            Employee employee = employeeService.findByEmail(email);
            if(employee != null){
                return new ResponseEntity<>(employee, HttpStatus.OK);
            }else{
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

        }catch (Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }


    @PostMapping()
    public ResponseEntity saveEmployee(@RequestBody Employee employee){
        try {
            employeeService.save(employee);
            return new ResponseEntity(HttpStatus.CREATED);
        }catch (Exception e){
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{email}")
    public ResponseEntity<Employee> updateEmployee(@RequestBody Employee employee, @PathVariable(value = "email") String email){
        try{
            Employee employeeFromDb = employeeService.findByEmail(email);
            if(employeeFromDb != null){
                employee.setId(employeeFromDb.getId());
                Employee updatedEmployee = employeeService.update(employee);
                return new ResponseEntity<>(updatedEmployee, HttpStatus.OK);
            }else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        }catch (Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{email}")
    public ResponseEntity<String> deleteTimeSheet(@PathVariable(value="email") String email){
        try{
            Employee employee = employeeService.findByEmail(email);
            if(employee != null){
                employeeService.delete(employee.getId());
                return new ResponseEntity<>("Employee with email " + email + "was successfully deleted", HttpStatus.OK );
            }else {
                return new ResponseEntity<>("Employee with email " + email + "unsuccessfully deleted", HttpStatus.NOT_FOUND);
            }
        }catch (Exception e){
            return new ResponseEntity<>(e.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }


}
