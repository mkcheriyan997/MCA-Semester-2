package com.mkc.lifeload.dto;

public class LoginResponseDTO {
    private String jwt;

    public LoginResponseDTO(String jwt) {
        this.jwt = jwt;
    }

    // Getter
    public String getJwt() {
        return jwt;
    }
}