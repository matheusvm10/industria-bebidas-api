package br.com.senai.bebidas.dto.response;

import br.com.senai.bebidas.entity.Movimentacao;
import br.com.senai.bebidas.enums.UnidadeMedida;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

public record MovimentacaoResponse(
        Long id,
        Long insumoId,
        String tipo,
        @JsonFormat(shape = JsonFormat.Shape.NUMBER)
        BigDecimal quantidade,
        Long setorId,
        Long usuarioId,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        LocalDate dataMovimentacao,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
        OffsetDateTime dataRegistro,
        String lote,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        LocalDate validade,
        @JsonFormat(shape = JsonFormat.Shape.NUMBER)
        BigDecimal saldoAnterior,
        @JsonFormat(shape = JsonFormat.Shape.NUMBER)
        BigDecimal saldoAtual
) {
    public static MovimentacaoResponse from(Movimentacao m) {
        UnidadeMedida u = m.getInsumo().getUnidadeMedida();
        return new MovimentacaoResponse(
                m.getId(),
                m.getInsumo().getId(),
                m.getTipo().name(),
                InsumoResponse.formatarNumero(m.getQuantidade(), u),
                m.getSetor() != null ? m.getSetor().getId() : null,
                m.getUsuario().getId(),
                m.getDataMovimentacao(),
                m.getDataRegistro(),
                m.getLote(),
                m.getValidade(),
                InsumoResponse.formatarNumero(m.getSaldoAnterior(), u),
                InsumoResponse.formatarNumero(m.getSaldoAtual(), u)
        );
    }
}
