package br.com.senai.bebidas.dto.response;

import br.com.senai.bebidas.entity.Setor;

public record SetorResponse(
        Long id,
        String nome,
        String descricao,
        Boolean ativo
) {
    public static SetorResponse from(Setor s) {
        return new SetorResponse(s.getId(), s.getNome(), s.getDescricao(), s.getAtivo());
    }
}
