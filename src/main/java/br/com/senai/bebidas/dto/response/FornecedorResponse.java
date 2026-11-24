package br.com.senai.bebidas.dto.response;

import br.com.senai.bebidas.entity.Fornecedor;

public record FornecedorResponse(
        Long id,
        String nome,
        String cnpj,
        String telefone,
        Boolean ativo
) {
    public static FornecedorResponse from(Fornecedor f) {
        return new FornecedorResponse(f.getId(), f.getNome(), f.getCnpj(), f.getTelefone(), f.getAtivo());
    }
}
