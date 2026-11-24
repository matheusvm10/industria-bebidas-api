package br.com.senai.bebidas.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record MovimentacaoCreateRequest(
        @NotNull(message = "O campo insumoId é obrigatório")
        Long insumoId,

        @NotBlank(message = "O campo tipo é obrigatório")
        String tipo,

        @NotNull(message = "O campo quantidade é obrigatório")
        BigDecimal quantidade,

        Long setorId,

        @NotNull(message = "O campo dataMovimentacao é obrigatório")
        LocalDate dataMovimentacao,

        @Size(max = 50, message = "O lote deve ter no máximo 50 caracteres")
        String lote,

        LocalDate validade
) {}
