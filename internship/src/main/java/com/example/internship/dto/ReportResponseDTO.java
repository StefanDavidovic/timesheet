package com.example.internship.dto;

import com.example.internship.model.Category;
import com.example.internship.model.Project;
import com.example.internship.model.TeamMember;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportResponseDTO {

    private Long id;
    private LocalDate date;
    private TeamMember teamMember;
    private Project project;
    private Category category;
    private String description;
    private float time;


}
