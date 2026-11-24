package br.com.senai.bebidas.repository;

import br.com.senai.bebidas.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByLogin(String login);
    boolean existsByLogin(String login);
    boolean existsByLoginAndIdNot(String login, Long id);
    List<Usuario> findAllByOrderByIdAsc();
    List<Usuario> findByAtivoOrderByIdAsc(Boolean ativo);
}
