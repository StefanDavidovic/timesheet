package com.example.internship.repository;

import com.example.internship.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface RefreshTokenRepo extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findById(Long id);
    Optional<RefreshToken> findByToken(String token);

    int deleteByTeamMemberId(Long userId);
}
