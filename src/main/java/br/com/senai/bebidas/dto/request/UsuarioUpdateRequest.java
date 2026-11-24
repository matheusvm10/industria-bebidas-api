package br.com.senai.bebidas.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UsuarioUpdateRequest(
        @NotBlank(message = "O campo nome é obrigatório")
        @Size(max = 120, message = "O nome deve ter no máximo 120 caracteres")
        String nome,

        @NotBlank(message = "O campo login é obrigatório")
        @Size(max = 60, message = "O login deve ter no máximo 60 caracteres")
        String login,

        @NotNull(message = "O campo ativo é obrigatório")
        Boolean ativo
) {}
