package com.example.internship.service.impl;

import com.example.internship.exception.TokenRefreshException;
import com.example.internship.model.RefreshToken;
import com.example.internship.repository.RefreshTokenRepo;
import com.example.internship.repository.TeamMemberRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    @Value("${internship.app.jwtRefreshExpirationMs}")
    private Long refreshTokenDuration;
    private final RefreshTokenRepo refreshTokenRepo;
    private final TeamMemberRepo teamMemberRepo;

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepo.findByToken(token);
    }

    public RefreshToken createRefreshToken(Long userId) {
        var refreshToken = new RefreshToken();
        refreshToken.setTeamMember(teamMemberRepo.findById(userId).get());
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDuration));
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken = refreshTokenRepo.save(refreshToken);
        return refreshToken;
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepo.delete(token);
            throw new TokenRefreshException(token.getToken(), "Refresh token was expired. Please make a new signin request");
        }

        return token;
    }

    @Transactional
    public int deleteByUserId(Long userId) {
        return refreshTokenRepo.deleteByTeamMemberId(teamMemberRepo.findById(userId).get().getId());
    }
}
