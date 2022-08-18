package com.example.internship.service.impl;

import com.example.internship.dto.JwtRequestDTO;
import com.example.internship.dto.JwtResponseDTO;
import com.example.internship.dto.RefreshTokenRequestDTO;
import com.example.internship.dto.RefreshTokenResponseDTO;
import com.example.internship.exception.ResourceNotFoundException;
import com.example.internship.exception.TokenRefreshException;
import com.example.internship.model.RefreshToken;
import com.example.internship.repository.TeamMemberRepo;
import com.example.internship.security.JwtUtil;
import com.example.internship.security.MyUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl {
    @Autowired
    private Logger log;
    private final TeamMemberRepo teamMemberRepo;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtTokenUtil;

    private final UserDetailsServiceImpl userDetailsService;
    private final RefreshTokenService refreshTokenService;

    private final PasswordEncoder encoder;


    public JwtResponseDTO login(JwtRequestDTO jwtRequestDTO){
        var user = teamMemberRepo.findByUsername(jwtRequestDTO.getUsername());
        log.info("Found Team member with username: " + jwtRequestDTO.getUsername());
        if(user != null) {

            var authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(jwtRequestDTO.getUsername(), jwtRequestDTO.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            var userDetails =  (MyUserDetails) authentication.getPrincipal();

            var jwt = jwtTokenUtil.generateToken(userDetails);
            var refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());

            return new JwtResponseDTO(jwt, refreshToken.getToken());
        }else {
            log.warning("User with username: " + jwtRequestDTO.getUsername() + " doesn't exist");
            throw new ResourceNotFoundException("Does not exist user with username" + jwtRequestDTO.getUsername());
        }
    }

    public RefreshTokenResponseDTO refreshToken(RefreshTokenRequestDTO request){
        var requestRefreshToken = request.getRefreshToken();
        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getTeamMember)
                .map(user -> {
                    var token = jwtTokenUtil.generateTokenFromUsername(user.getUsername());
                    log.info("Created new token: ");
                    return new RefreshTokenResponseDTO(token, requestRefreshToken);
                }).orElseThrow(() -> new TokenRefreshException(requestRefreshToken, "Refresh token is not in database!"));
    }

}
