package br.com.senai.bebidas.repository;

import br.com.senai.bebidas.entity.Movimentacao;
import br.com.senai.bebidas.enums.TipoMovimentacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface MovimentacaoRepository extends JpaRepository<Movimentacao, Long> {

    boolean existsByInsumoId(Long insumoId);
    boolean existsBySetorId(Long setorId);

    @Query("SELECT m FROM Movimentacao m " +
           "WHERE (:insumoId IS NULL OR m.insumo.id = :insumoId) " +
           "  AND (:setorId IS NULL OR m.setor.id = :setorId) " +
           "  AND (:usuarioId IS NULL OR m.usuario.id = :usuarioId) " +
           "  AND (:tipo IS NULL OR m.tipo = :tipo) " +
           "  AND (:dataInicio IS NULL OR m.dataMovimentacao >= :dataInicio) " +
           "  AND (:dataFim IS NULL OR m.dataMovimentacao <= :dataFim) " +
           "ORDER BY m.dataRegistro DESC, m.id DESC")
    List<Movimentacao> filtrar(
            @Param("insumoId") Long insumoId,
            @Param("setorId") Long setorId,
            @Param("usuarioId") Long usuarioId,
            @Param("tipo") TipoMovimentacao tipo,
            @Param("dataInicio") LocalDate dataInicio,
            @Param("dataFim") LocalDate dataFim
    );
}
