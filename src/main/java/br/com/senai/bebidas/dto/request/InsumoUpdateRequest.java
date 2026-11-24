package br.com.senai.bebidas.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record InsumoUpdateRequest(
        @NotBlank(message = "O campo nome é obrigatório")
        @Size(max = 150, message = "O nome deve ter no máximo 150 caracteres")
        String nome,

        @NotBlank(message = "O campo unidadeMedida é obrigatório")
        String unidadeMedida,

        @NotNull(message = "O campo estoqueMinimo é obrigatório")
        @DecimalMin(value = "0.000", message = "O estoque mínimo não pode ser negativo")
        BigDecimal estoqueMinimo,

        @NotNull(message = "O campo fornecedorId é obrigatório")
        Long fornecedorId,

        @NotNull(message = "O campo ativo é obrigatório")
        Boolean ativo
) {}
