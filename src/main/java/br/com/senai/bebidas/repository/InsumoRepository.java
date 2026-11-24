package br.com.senai.bebidas.repository;

import br.com.senai.bebidas.entity.Insumo;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface InsumoRepository extends JpaRepository<Insumo, Long> {

    boolean existsByFornecedorId(Long fornecedorId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT i FROM Insumo i WHERE i.id = :id")
    Optional<Insumo> findByIdWithLock(@Param("id") Long id);

    @Query("SELECT i FROM Insumo i " +
           "WHERE (:ativo IS NULL OR i.ativo = :ativo) " +
           "  AND (:nome IS NULL OR LOWER(i.nome) LIKE LOWER(CONCAT('%', :nome, '%'))) " +
           "ORDER BY i.nome ASC")
    List<Insumo> buscarComFiltros(@Param("ativo") Boolean ativo, @Param("nome") String nome);

    @Query("SELECT i FROM Insumo i " +
           "WHERE i.ativo = true " +
           "  AND i.quantidadeAtual <= i.estoqueMinimo " +
           "ORDER BY i.nome ASC")
    List<Insumo> findAlertasCriticos();
}
