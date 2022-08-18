package com.example.internship.repository;

import com.example.internship.model.TeamMember;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface TeamMemberRepo extends JpaRepository<TeamMember, Long> {
    Page<TeamMember> findAll(Pageable pageable);
    TeamMember findByUsername(String username);

    Optional<TeamMember> findByEmail(String email);

}
