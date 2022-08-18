package com.example.employees;

import com.example.employees.repository.EmployeeRepo;
import com.example.employees.serverGrpc.TeamMemberImpl;
import com.example.employees.service.EmployeeService;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import java.io.IOException;

@SpringBootApplication
public class EmployeesApplication {

	public static void main(String[] args) throws IOException, InterruptedException {
		SpringApplication.run(EmployeesApplication.class, args);
	}

}
