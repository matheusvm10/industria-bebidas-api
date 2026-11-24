package br.com.senai.bebidas.dto.response;

import br.com.senai.bebidas.entity.Usuario;

public record UsuarioResponse(
        Long id,
        String nome,
        String login,
        Boolean ativo
) {
    public static UsuarioResponse from(Usuario u) {
        return new UsuarioResponse(u.getId(), u.getNome(), u.getLogin(), u.getAtivo());
    }
}
