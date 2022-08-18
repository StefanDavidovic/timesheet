package com.example.internship.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeamMemberDTO {

    private Long id;
    private String name;
    private String username;
    private Integer hoursPerWeek;
    private String email;
    private String password;
    private String role;
    private boolean status;
    private boolean archive;

}
