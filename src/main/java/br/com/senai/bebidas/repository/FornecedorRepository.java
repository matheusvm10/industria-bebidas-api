package br.com.senai.bebidas.repository;

import br.com.senai.bebidas.entity.Fornecedor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FornecedorRepository extends JpaRepository<Fornecedor, Long> {
    boolean existsByCnpj(String cnpj);
    boolean existsByCnpjAndIdNot(String cnpj, Long id);
    List<Fornecedor> findAllByOrderByIdAsc();
    List<Fornecedor> findByAtivoOrderByIdAsc(Boolean ativo);
}
