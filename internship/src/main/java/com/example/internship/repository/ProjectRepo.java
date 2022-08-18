package com.example.internship.repository;

import com.example.internship.model.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRepo extends JpaRepository<Project, Long> {
    Page<Project> findAll(Pageable pageable);
    Project findByName(String name);
}
