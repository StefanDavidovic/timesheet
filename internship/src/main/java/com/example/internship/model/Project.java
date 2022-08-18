package com.example.internship.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    private String description;

    private boolean status;

    private boolean archive;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Client customer;
    @ManyToOne
    @JoinColumn(name = "teamMember_id", nullable = false)
    private TeamMember teamMember;

    @Version
    private Integer version;


}
