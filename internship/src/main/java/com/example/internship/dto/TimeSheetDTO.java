package com.example.internship.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TimeSheetDTO {

    private Long id;
    private LocalDate date;
    private String description;
    private float time;
    private float overtime;
    private Long client;
    private Long project;
    private Long category;

}
