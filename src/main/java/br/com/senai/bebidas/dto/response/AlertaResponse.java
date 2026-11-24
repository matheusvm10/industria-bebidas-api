package br.com.senai.bebidas.dto.response;

import br.com.senai.bebidas.entity.Insumo;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;

public record AlertaResponse(
        Long insumoId,
        String nome,
        @JsonFormat(shape = JsonFormat.Shape.NUMBER)
        BigDecimal quantidadeAtual,
        @JsonFormat(shape = JsonFormat.Shape.NUMBER)
        BigDecimal estoqueMinimo,
        String situacao
) {
    public static AlertaResponse from(Insumo i) {
        return new AlertaResponse(
                i.getId(),
                i.getNome(),
                InsumoResponse.formatarNumero(i.getQuantidadeAtual(), i.getUnidadeMedida()),
                InsumoResponse.formatarNumero(i.getEstoqueMinimo(), i.getUnidadeMedida()),
                "CRITICO"
        );
    }
}
