package br.com.senai.bebidas.service;

import br.com.senai.bebidas.dto.request.FornecedorCreateRequest;
import br.com.senai.bebidas.dto.request.FornecedorUpdateRequest;
import br.com.senai.bebidas.dto.response.FornecedorResponse;
import br.com.senai.bebidas.entity.Fornecedor;
import br.com.senai.bebidas.exception.BusinessConflictException;
import br.com.senai.bebidas.exception.ResourceNotFoundException;
import br.com.senai.bebidas.repository.FornecedorRepository;
import br.com.senai.bebidas.repository.InsumoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FornecedorService {

    private final FornecedorRepository fornecedorRepository;
    private final InsumoRepository insumoRepository;

    public FornecedorService(FornecedorRepository fornecedorRepository,
                             InsumoRepository insumoRepository) {
        this.fornecedorRepository = fornecedorRepository;
        this.insumoRepository = insumoRepository;
    }

    @Transactional
    public FornecedorResponse criar(FornecedorCreateRequest req) {
        if (fornecedorRepository.existsByCnpj(req.cnpj())) {
            throw new BusinessConflictException("CNPJ já cadastrado");
        }
        Fornecedor fornecedor = new Fornecedor(req.nome().trim(), req.cnpj(), req.telefone());
        return FornecedorResponse.from(fornecedorRepository.save(fornecedor));
    }

    @Transactional(readOnly = true)
    public List<FornecedorResponse> listar(String ativoParam) {
        Boolean ativo = FiltroAtivoHelper.resolver(ativoParam);
        List<Fornecedor> lista = (ativo == null)
                ? fornecedorRepository.findAllByOrderByIdAsc()
                : fornecedorRepository.findByAtivoOrderByIdAsc(ativo);
        return lista.stream().map(FornecedorResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public FornecedorResponse buscarPorId(Long id) {
        Fornecedor fornecedor = fornecedorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fornecedor não encontrado"));
        return FornecedorResponse.from(fornecedor);
    }

    @Transactional
    public FornecedorResponse atualizar(Long id, FornecedorUpdateRequest req) {
        Fornecedor fornecedor = fornecedorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fornecedor não encontrado"));

        if (fornecedorRepository.existsByCnpjAndIdNot(req.cnpj(), id)) {
            throw new BusinessConflictException("CNPJ já cadastrado");
        }

        fornecedor.setNome(req.nome().trim());
        fornecedor.setCnpj(req.cnpj());
        fornecedor.setTelefone(req.telefone());
        fornecedor.setAtivo(req.ativo());
        return FornecedorResponse.from(fornecedorRepository.save(fornecedor));
    }

    @Transactional
    public void excluirOuInativar(Long id) {
        Fornecedor fornecedor = fornecedorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fornecedor não encontrado"));

        if (insumoRepository.existsByFornecedorId(id)) {
            fornecedor.setAtivo(false);
            fornecedorRepository.save(fornecedor);
        } else {
            fornecedorRepository.delete(fornecedor);
        }
    }
}
