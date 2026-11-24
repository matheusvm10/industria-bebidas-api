package br.com.senai.bebidas.service;

import br.com.senai.bebidas.dto.request.InsumoCreateRequest;
import br.com.senai.bebidas.dto.request.InsumoUpdateRequest;
import br.com.senai.bebidas.dto.response.AlertaResponse;
import br.com.senai.bebidas.dto.response.InsumoResponse;
import br.com.senai.bebidas.entity.Fornecedor;
import br.com.senai.bebidas.entity.Insumo;
import br.com.senai.bebidas.enums.UnidadeMedida;
import br.com.senai.bebidas.exception.BadRequestException;
import br.com.senai.bebidas.exception.BusinessConflictException;
import br.com.senai.bebidas.exception.ResourceNotFoundException;
import br.com.senai.bebidas.repository.FornecedorRepository;
import br.com.senai.bebidas.repository.InsumoRepository;
import br.com.senai.bebidas.repository.MovimentacaoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class InsumoService {

    private final InsumoRepository insumoRepository;
    private final FornecedorRepository fornecedorRepository;
    private final MovimentacaoRepository movimentacaoRepository;

    public InsumoService(InsumoRepository insumoRepository,
                         FornecedorRepository fornecedorRepository,
                         MovimentacaoRepository movimentacaoRepository) {
        this.insumoRepository = insumoRepository;
        this.fornecedorRepository = fornecedorRepository;
        this.movimentacaoRepository = movimentacaoRepository;
    }

    @Transactional
    public InsumoResponse criar(InsumoCreateRequest req) {
        if (req.quantidadeAtual() != null) {
            throw new BadRequestException("Não é permitido informar quantidadeAtual no cadastro de insumo");
        }

        UnidadeMedida unidade = parseUnidade(req.unidadeMedida());
        validarEscalaUnidade(req.estoqueMinimo(), unidade, "estoqueMinimo");

        Fornecedor fornecedor = fornecedorRepository.findById(req.fornecedorId())
                .orElseThrow(() -> new ResourceNotFoundException("Fornecedor não encontrado"));

        if (!Boolean.TRUE.equals(fornecedor.getAtivo())) {
            throw new BusinessConflictException("Fornecedor inativo não pode ser vinculado a um insumo");
        }

        Insumo insumo = new Insumo();
        insumo.setNome(req.nome().trim());
        insumo.setUnidadeMedida(unidade);
        insumo.setQuantidadeAtual(BigDecimal.ZERO);
        insumo.setEstoqueMinimo(req.estoqueMinimo());
        insumo.setFornecedor(fornecedor);
        insumo.setAtivo(true);

        return InsumoResponse.from(insumoRepository.save(insumo));
    }

    @Transactional(readOnly = true)
    public List<InsumoResponse> listar(String nome, String ativoParam) {
        Boolean ativo = FiltroAtivoHelper.resolver(ativoParam);
        String filtroNome = (nome != null && !nome.isBlank()) ? nome.trim() : null;
        return insumoRepository.buscarComFiltros(ativo, filtroNome)
                .stream()
                .map(InsumoResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public InsumoResponse buscarPorId(Long id) {
        Insumo insumo = insumoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Insumo não encontrado"));
        return InsumoResponse.from(insumo);
    }

    @Transactional
    public InsumoResponse atualizar(Long id, InsumoUpdateRequest req) {
        Insumo insumo = insumoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Insumo não encontrado"));

        UnidadeMedida novaUnidade = parseUnidade(req.unidadeMedida());
        validarEscalaUnidade(req.estoqueMinimo(), novaUnidade, "estoqueMinimo");

        if (insumo.getUnidadeMedida() != novaUnidade && movimentacaoRepository.existsByInsumoId(id)) {
            throw new BusinessConflictException("Unidade de medida não pode ser alterada após existir movimentação");
        }

        if (!insumo.getFornecedor().getId().equals(req.fornecedorId())) {
            Fornecedor novoFornecedor = fornecedorRepository.findById(req.fornecedorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Fornecedor não encontrado"));
            if (!Boolean.TRUE.equals(novoFornecedor.getAtivo())) {
                throw new BusinessConflictException("Fornecedor inativo não pode ser atribuído a um insumo");
            }
            insumo.setFornecedor(novoFornecedor);
        }

        insumo.setNome(req.nome().trim());
        insumo.setUnidadeMedida(novaUnidade);
        insumo.setEstoqueMinimo(req.estoqueMinimo());
        insumo.setAtivo(req.ativo());

        return InsumoResponse.from(insumoRepository.save(insumo));
    }

    @Transactional
    public void excluirOuInativar(Long id) {
        Insumo insumo = insumoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Insumo não encontrado"));

        if (movimentacaoRepository.existsByInsumoId(id)) {
            insumo.setAtivo(false);
            insumoRepository.save(insumo);
        } else {
            insumoRepository.delete(insumo);
        }
    }

    @Transactional(readOnly = true)
    public List<AlertaResponse> listarAlertas() {
        return insumoRepository.findAlertasCriticos()
                .stream()
                .map(AlertaResponse::from)
                .toList();
    }

    public static UnidadeMedida parseUnidade(String valor) {
        try {
            return UnidadeMedida.valueOf(valor.trim().toUpperCase());
        } catch (Exception e) {
            throw new BadRequestException("Unidade de medida inválida. Valores aceitos: UN, KG, L");
        }
    }

    public static void validarEscalaUnidade(BigDecimal valor, UnidadeMedida unidade, String campo) {
        BigDecimal normalizado = valor.stripTrailingZeros();
        int casasDecimais = Math.max(normalizado.scale(), 0);

        if (unidade == UnidadeMedida.UN && casasDecimais > 0) {
            throw new BadRequestException("Para unidade UN, o campo " + campo + " deve ser um número inteiro");
        }
        if ((unidade == UnidadeMedida.KG || unidade == UnidadeMedida.L) && casasDecimais > 3) {
            throw new BadRequestException("Para unidade " + unidade + ", o campo " + campo + " admite até 3 casas decimais");
        }
    }
}
