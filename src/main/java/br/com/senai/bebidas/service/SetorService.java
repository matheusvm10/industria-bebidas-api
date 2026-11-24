package br.com.senai.bebidas.service;

import br.com.senai.bebidas.dto.request.SetorCreateRequest;
import br.com.senai.bebidas.dto.request.SetorUpdateRequest;
import br.com.senai.bebidas.dto.response.SetorResponse;
import br.com.senai.bebidas.entity.Setor;
import br.com.senai.bebidas.exception.BusinessConflictException;
import br.com.senai.bebidas.exception.ResourceNotFoundException;
import br.com.senai.bebidas.repository.MovimentacaoRepository;
import br.com.senai.bebidas.repository.SetorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SetorService {

    private final SetorRepository setorRepository;
    private final MovimentacaoRepository movimentacaoRepository;

    public SetorService(SetorRepository setorRepository,
                        MovimentacaoRepository movimentacaoRepository) {
        this.setorRepository = setorRepository;
        this.movimentacaoRepository = movimentacaoRepository;
    }

    @Transactional
    public SetorResponse criar(SetorCreateRequest req) {
        String nome = req.nome().trim();
        if (setorRepository.existsByNomeIgnoreCaseAndAtivoTrue(nome)) {
            throw new BusinessConflictException("Já existe um setor ativo com este nome");
        }
        Setor setor = new Setor(nome, req.descricao());
        return SetorResponse.from(setorRepository.save(setor));
    }

    @Transactional(readOnly = true)
    public List<SetorResponse> listar(String ativoParam) {
        Boolean ativo = FiltroAtivoHelper.resolver(ativoParam);
        List<Setor> lista = (ativo == null)
                ? setorRepository.findAllByOrderByIdAsc()
                : setorRepository.findByAtivoOrderByIdAsc(ativo);
        return lista.stream().map(SetorResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public SetorResponse buscarPorId(Long id) {
        Setor setor = setorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Setor não encontrado"));
        return SetorResponse.from(setor);
    }

    @Transactional
    public SetorResponse atualizar(Long id, SetorUpdateRequest req) {
        Setor setor = setorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Setor não encontrado"));

        String nome = req.nome().trim();
        if (Boolean.TRUE.equals(req.ativo()) &&
                setorRepository.existsByNomeIgnoreCaseAndAtivoTrueAndIdNot(nome, id)) {
            throw new BusinessConflictException("Já existe um setor ativo com este nome");
        }

        setor.setNome(nome);
        setor.setDescricao(req.descricao());
        setor.setAtivo(req.ativo());
        return SetorResponse.from(setorRepository.save(setor));
    }

    @Transactional
    public void excluirOuInativar(Long id) {
        Setor setor = setorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Setor não encontrado"));

        if (movimentacaoRepository.existsBySetorId(id)) {
            setor.setAtivo(false);
            setorRepository.save(setor);
        } else {
            setorRepository.delete(setor);
        }
    }
}
