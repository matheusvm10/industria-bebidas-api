package br.com.senai.bebidas.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UsuarioCreateRequest(
        @NotBlank(message = "O campo nome é obrigatório")
        @Size(max = 120, message = "O nome deve ter no máximo 120 caracteres")
        String nome,

        @NotBlank(message = "O campo login é obrigatório")
        @Size(max = 60, message = "O login deve ter no máximo 60 caracteres")
        String login,

        @NotBlank(message = "O campo senha é obrigatório")
        @Size(min = 8, message = "A senha deve ter no mínimo 8 caracteres")
        String senha
) {}
