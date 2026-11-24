package br.com.senai.bebidas.dto.response;

import br.com.senai.bebidas.entity.Insumo;
import br.com.senai.bebidas.enums.UnidadeMedida;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;

public record InsumoResponse(
        Long id,
        String nome,
        String unidadeMedida,
        @JsonFormat(shape = JsonFormat.Shape.NUMBER)
        BigDecimal quantidadeAtual,
        @JsonFormat(shape = JsonFormat.Shape.NUMBER)
        BigDecimal estoqueMinimo,
        Long fornecedorId,
        Boolean ativo
) {
    public static InsumoResponse from(Insumo i) {
        return new InsumoResponse(
                i.getId(),
                i.getNome(),
                i.getUnidadeMedida().name(),
                formatarNumero(i.getQuantidadeAtual(), i.getUnidadeMedida()),
                formatarNumero(i.getEstoqueMinimo(), i.getUnidadeMedida()),
                i.getFornecedor().getId(),
                i.getAtivo()
        );
    }

    public static BigDecimal formatarNumero(BigDecimal valor, UnidadeMedida unidade) {
        if (valor == null) return BigDecimal.ZERO;
        BigDecimal stripped = valor.stripTrailingZeros();
        return stripped.scale() < 0 ? stripped.setScale(0) : stripped;
    }
}
