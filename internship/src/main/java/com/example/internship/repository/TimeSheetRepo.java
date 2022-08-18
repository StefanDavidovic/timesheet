package com.example.internship.repository;

import com.example.internship.model.TimeSheet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;


public interface TimeSheetRepo extends JpaRepository<TimeSheet, Long> {

    List<TimeSheet> findByProjectId(Long id);
    List<TimeSheet> findByClientId(Long id);
    List<TimeSheet> findByCategoryId(Long id);
    List<TimeSheet> findByDate(LocalDate date);

}
