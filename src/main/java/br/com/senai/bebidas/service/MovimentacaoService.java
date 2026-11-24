package br.com.senai.bebidas.service;

import br.com.senai.bebidas.dto.request.MovimentacaoCreateRequest;
import br.com.senai.bebidas.dto.response.MovimentacaoResponse;
import br.com.senai.bebidas.entity.Insumo;
import br.com.senai.bebidas.entity.Movimentacao;
import br.com.senai.bebidas.entity.Setor;
import br.com.senai.bebidas.entity.Usuario;
import br.com.senai.bebidas.enums.TipoMovimentacao;
import br.com.senai.bebidas.exception.BadRequestException;
import br.com.senai.bebidas.exception.BusinessConflictException;
import br.com.senai.bebidas.exception.ResourceNotFoundException;
import br.com.senai.bebidas.repository.InsumoRepository;
import br.com.senai.bebidas.repository.MovimentacaoRepository;
import br.com.senai.bebidas.repository.SetorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

@Service
public class MovimentacaoService {

    private final MovimentacaoRepository movimentacaoRepository;
    private final InsumoRepository insumoRepository;
    private final SetorRepository setorRepository;
    private final UsuarioService usuarioService;

    public MovimentacaoService(MovimentacaoRepository movimentacaoRepository,
                               InsumoRepository insumoRepository,
                               SetorRepository setorRepository,
                               UsuarioService usuarioService) {
        this.movimentacaoRepository = movimentacaoRepository;
        this.insumoRepository = insumoRepository;
        this.setorRepository = setorRepository;
        this.usuarioService = usuarioService;
    }

    @Transactional
    public MovimentacaoResponse registrar(MovimentacaoCreateRequest req) {
        TipoMovimentacao tipo = parseTipo(req.tipo());

        if (req.quantidade().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("A quantidade deve ser maior que zero");
        }

        if (req.dataMovimentacao().isAfter(LocalDate.now())) {
            throw new BadRequestException("A data da movimentação não pode ser futura");
        }

        Setor setor = null;
        if (tipo == TipoMovimentacao.SAIDA) {
            if (req.setorId() == null) {
                throw new BadRequestException("O campo setorId é obrigatório para movimentação de SAIDA");
            }
            if ((req.lote() != null && !req.lote().isBlank()) || req.validade() != null) {
                throw new BadRequestException("Movimentação de SAIDA não permite informar lote ou validade");
            }
            setor = setorRepository.findById(req.setorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Setor não encontrado"));
            if (!Boolean.TRUE.equals(setor.getAtivo())) {
                throw new BusinessConflictException("Setor inativo");
            }
        } else {
            if (req.setorId() != null) {
                throw new BadRequestException("Movimentação de ENTRADA não deve informar setorId");
            }
            if (req.validade() != null && req.validade().isBefore(req.dataMovimentacao())) {
                throw new BadRequestException("A data de validade não pode ser anterior à data da movimentação");
            }
        }

        Usuario usuarioAutenticado = usuarioService.getUsuarioAutenticadoEntity();

        Insumo insumo = insumoRepository.findByIdWithLock(req.insumoId())
                .orElseThrow(() -> new ResourceNotFoundException("Insumo não encontrado"));

        if (!Boolean.TRUE.equals(insumo.getAtivo())) {
            throw new BusinessConflictException("Insumo inativo");
        }

        InsumoService.validarEscalaUnidade(req.quantidade(), insumo.getUnidadeMedida(), "quantidade");

        BigDecimal saldoAnterior = insumo.getQuantidadeAtual();
        insumo.aplicar(tipo, req.quantidade());
        BigDecimal saldoPosterior = insumo.getQuantidadeAtual();

        insumoRepository.save(insumo);

        String loteLimpo = (req.lote() != null && !req.lote().isBlank()) ? req.lote().trim() : null;

        Movimentacao movimentacao = new Movimentacao(
                insumo,
                usuarioAutenticado,
                setor,
                tipo,
                req.quantidade(),
                req.dataMovimentacao(),
                OffsetDateTime.now(),
                loteLimpo,
                req.validade(),
                saldoAnterior,
                saldoPosterior
        );

        return MovimentacaoResponse.from(movimentacaoRepository.save(movimentacao));
    }

    @Transactional(readOnly = true)
    public List<MovimentacaoResponse> listar(Long insumoId,
                                             Long setorId,
                                             Long usuarioId,
                                             String tipoParam,
                                             LocalDate dataInicio,
                                             LocalDate dataFim) {
        if (dataInicio != null && dataFim != null && dataInicio.isAfter(dataFim)) {
            throw new BadRequestException("A dataInicio não pode ser posterior à dataFim");
        }

        TipoMovimentacao tipo = (tipoParam != null && !tipoParam.isBlank()) ? parseTipo(tipoParam) : null;

        return movimentacaoRepository.filtrar(insumoId, setorId, usuarioId, tipo, dataInicio, dataFim)
                .stream()
                .map(MovimentacaoResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public MovimentacaoResponse buscarPorId(Long id) {
        Movimentacao mov = movimentacaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movimentação não encontrada"));
        return MovimentacaoResponse.from(mov);
    }

    private TipoMovimentacao parseTipo(String valor) {
        try {
            return TipoMovimentacao.valueOf(valor.trim().toUpperCase());
        } catch (Exception e) {
            throw new BadRequestException("Tipo de movimentação inválido. Valores aceitos: ENTRADA, SAIDA");
        }
    }
}
