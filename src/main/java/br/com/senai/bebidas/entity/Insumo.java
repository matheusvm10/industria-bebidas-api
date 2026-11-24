package br.com.senai.bebidas.entity;

import br.com.senai.bebidas.enums.TipoMovimentacao;
import br.com.senai.bebidas.enums.UnidadeMedida;
import br.com.senai.bebidas.exception.BusinessConflictException;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "insumo")
public class Insumo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(name = "unidade_medida", nullable = false, length = 2)
    private UnidadeMedida unidadeMedida;

    @Column(name = "quantidade_atual", nullable = false, precision = 15, scale = 3)
    private BigDecimal quantidadeAtual = BigDecimal.ZERO;

    @Column(name = "estoque_minimo", nullable = false, precision = 15, scale = 3)
    private BigDecimal estoqueMinimo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fornecedor_id", nullable = false)
    private Fornecedor fornecedor;

    @Column(nullable = false)
    private Boolean ativo = true;

    public Insumo() {}

    public void aplicar(TipoMovimentacao tipo, BigDecimal quantidade) {
        if (tipo == TipoMovimentacao.ENTRADA) {
            this.quantidadeAtual = this.quantidadeAtual.add(quantidade);
        } else {
            if (this.quantidadeAtual.compareTo(quantidade) < 0) {
                throw new BusinessConflictException("Quantidade solicitada superior ao estoque disponível");
            }
            this.quantidadeAtual = this.quantidadeAtual.subtract(quantidade);
        }
    }

    public boolean emAlerta() {
        return Boolean.TRUE.equals(this.ativo) && this.quantidadeAtual.compareTo(this.estoqueMinimo) <= 0;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public UnidadeMedida getUnidadeMedida() { return unidadeMedida; }
    public void setUnidadeMedida(UnidadeMedida unidadeMedida) { this.unidadeMedida = unidadeMedida; }

    public BigDecimal getQuantidadeAtual() { return quantidadeAtual; }
    public void setQuantidadeAtual(BigDecimal quantidadeAtual) { this.quantidadeAtual = quantidadeAtual; }

    public BigDecimal getEstoqueMinimo() { return estoqueMinimo; }
    public void setEstoqueMinimo(BigDecimal estoqueMinimo) { this.estoqueMinimo = estoqueMinimo; }

    public Fornecedor getFornecedor() { return fornecedor; }
    public void setFornecedor(Fornecedor fornecedor) { this.fornecedor = fornecedor; }

    public Boolean getAtivo() { return ativo; }
    public void setAtivo(Boolean ativo) { this.ativo = ativo; }
}
