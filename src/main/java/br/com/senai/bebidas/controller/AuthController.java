package br.com.senai.bebidas.controller;

import br.com.senai.bebidas.dto.request.LoginRequest;
import br.com.senai.bebidas.dto.response.TokenResponse;
import br.com.senai.bebidas.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UsuarioService usuarioService;

    public AuthController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(usuarioService.autenticar(request));
    }
}
