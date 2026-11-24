package br.com.senai.bebidas.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record FornecedorCreateRequest(
        @NotBlank(message = "O campo nome é obrigatório")
        @Size(max = 150, message = "O nome deve ter no máximo 150 caracteres")
        String nome,

        @NotBlank(message = "O campo cnpj é obrigatório")
        @Pattern(regexp = "^[0-9]{14}$", message = "O CNPJ deve conter exatamente 14 dígitos numéricos")
        String cnpj,

        @Size(max = 20, message = "O telefone deve ter no máximo 20 caracteres")
        String telefone
) {}
