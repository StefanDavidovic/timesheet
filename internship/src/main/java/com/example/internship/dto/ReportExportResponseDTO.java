package com.example.internship.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportExportResponseDTO {

    private Long id;
    private LocalDate date;
    private String teamMember;
    private String project;
    private String category;
    private String description;
    private float time;
}
