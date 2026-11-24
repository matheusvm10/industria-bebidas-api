package br.com.senai.bebidas.repository;

import br.com.senai.bebidas.entity.Setor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SetorRepository extends JpaRepository<Setor, Long> {
    boolean existsByNomeIgnoreCaseAndAtivoTrue(String nome);
    boolean existsByNomeIgnoreCaseAndAtivoTrueAndIdNot(String nome, Long id);
    List<Setor> findAllByOrderByIdAsc();
    List<Setor> findByAtivoOrderByIdAsc(Boolean ativo);
}
