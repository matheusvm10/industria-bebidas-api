package br.com.senai.bebidas.entity;

import br.com.senai.bebidas.enums.TipoMovimentacao;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Table(name = "movimentacao")
public class Movimentacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "insumo_id", nullable = false)
    private Insumo insumo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "setor_id")
    private Setor setor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 7)
    private TipoMovimentacao tipo;

    @Column(nullable = false, precision = 15, scale = 3)
    private BigDecimal quantidade;

    @Column(name = "data_operacional", nullable = false)
    private LocalDate dataMovimentacao;

    @Column(name = "data_hora_registro", nullable = false)
    private OffsetDateTime dataRegistro;

    @Column(length = 50)
    private String lote;

    @Column
    private LocalDate validade;

    @Column(name = "saldo_anterior", nullable = false, precision = 15, scale = 3)
    private BigDecimal saldoAnterior;

    @Column(name = "saldo_posterior", nullable = false, precision = 15, scale = 3)
    private BigDecimal saldoAtual;

    protected Movimentacao() {}

    public Movimentacao(Insumo insumo, Usuario usuario, Setor setor, TipoMovimentacao tipo,
                        BigDecimal quantidade, LocalDate dataMovimentacao, OffsetDateTime dataRegistro,
                        String lote, LocalDate validade, BigDecimal saldoAnterior, BigDecimal saldoAtual) {
        this.insumo = insumo;
        this.usuario = usuario;
        this.setor = setor;
        this.tipo = tipo;
        this.quantidade = quantidade;
        this.dataMovimentacao = dataMovimentacao;
        this.dataRegistro = dataRegistro;
        this.lote = lote;
        this.validade = validade;
        this.saldoAnterior = saldoAnterior;
        this.saldoAtual = saldoAtual;
    }

    public Long getId() { return id; }
    public Insumo getInsumo() { return insumo; }
    public Usuario getUsuario() { return usuario; }
    public Setor getSetor() { return setor; }
    public TipoMovimentacao getTipo() { return tipo; }
    public BigDecimal getQuantidade() { return quantidade; }
    public LocalDate getDataMovimentacao() { return dataMovimentacao; }
    public OffsetDateTime getDataRegistro() { return dataRegistro; }
    public String getLote() { return lote; }
    public LocalDate getValidade() { return validade; }
    public BigDecimal getSaldoAnterior() { return saldoAnterior; }
    public BigDecimal getSaldoAtual() { return saldoAtual; }
}
