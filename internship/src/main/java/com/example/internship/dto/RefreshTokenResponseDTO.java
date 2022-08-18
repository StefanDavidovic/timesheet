package com.example.internship.dto;

import lombok.Data;

@Data
public class RefreshTokenResponseDTO {

    private String accessToken;
    private String refreshToken;
    private String tokenType = "Bearer";

    public RefreshTokenResponseDTO(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

}
