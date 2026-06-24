package br.com.cooperativa.glosa.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "glosa_item")
public class GlosaItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "importacao_id")
    private GlosaImportacao importacao;

    private String guiaSenha;
    private LocalDate dataRealizacao;

    @Column(length = 200)
    private String procedimento;

    @Column(precision = 15, scale = 2) private BigDecimal valorInformado;
    @Column(precision = 15, scale = 2) private BigDecimal valorPago;
    @Column(precision = 15, scale = 2) private BigDecimal valorGlosa;
    private Double percentualGlosa;

    @Enumerated(EnumType.STRING) @Column(length = 20)
    private TipoGlosa tipoGlosa;

    @Enumerated(EnumType.STRING) @Column(length = 30)
    private SituacaoGlosa situacao;

    @Column(length = 500) private String motivoGlosa;
    @Column(length = 500) private String motivoBloqueio;

    public GlosaItem() {}

    public Long getId() { return id; }
    public GlosaImportacao getImportacao() { return importacao; }
    public void setImportacao(GlosaImportacao importacao) { this.importacao = importacao; }
    public String getGuiaSenha() { return guiaSenha; }
    public void setGuiaSenha(String guiaSenha) { this.guiaSenha = guiaSenha; }
    public LocalDate getDataRealizacao() { return dataRealizacao; }
    public void setDataRealizacao(LocalDate dataRealizacao) { this.dataRealizacao = dataRealizacao; }
    public String getProcedimento() { return procedimento; }
    public void setProcedimento(String procedimento) { this.procedimento = procedimento; }
    public BigDecimal getValorInformado() { return valorInformado; }
    public void setValorInformado(BigDecimal valorInformado) { this.valorInformado = valorInformado; }
    public BigDecimal getValorPago() { return valorPago; }
    public void setValorPago(BigDecimal valorPago) { this.valorPago = valorPago; }
    public BigDecimal getValorGlosa() { return valorGlosa; }
    public void setValorGlosa(BigDecimal valorGlosa) { this.valorGlosa = valorGlosa; }
    public Double getPercentualGlosa() { return percentualGlosa; }
    public void setPercentualGlosa(Double percentualGlosa) { this.percentualGlosa = percentualGlosa; }
    public TipoGlosa getTipoGlosa() { return tipoGlosa; }
    public void setTipoGlosa(TipoGlosa tipoGlosa) { this.tipoGlosa = tipoGlosa; }
    public SituacaoGlosa getSituacao() { return situacao; }
    public void setSituacao(SituacaoGlosa situacao) { this.situacao = situacao; }
    public String getMotivoGlosa() { return motivoGlosa; }
    public void setMotivoGlosa(String motivoGlosa) { this.motivoGlosa = motivoGlosa; }
    public String getMotivoBloqueio() { return motivoBloqueio; }
    public void setMotivoBloqueio(String motivoBloqueio) { this.motivoBloqueio = motivoBloqueio; }

    public boolean estaBloqueado() { return SituacaoGlosa.BLOQUEADA.equals(situacao); }
}
