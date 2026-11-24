package br.com.senai.bebidas.dto.response;

public record TokenResponse(
        String token,
        String tipo,
        long expiraEm
) {}
