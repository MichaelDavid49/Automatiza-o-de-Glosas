package br.com.cooperativa.glosa.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "glosa_importacao")
public class GlosaImportacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nomeArquivo;

    @Column(nullable = false)
    private LocalDateTime dataImportacao;

    @Column(nullable = false)
    private String operador;

    private Integer totalLinhas;
    private Integer processadas;
    private Integer bloqueadas;

    @OneToMany(mappedBy = "importacao", cascade = CascadeType.ALL,
               fetch = FetchType.LAZY, orphanRemoval = true)
    private List<GlosaItem> itens = new ArrayList<>();

    public GlosaImportacao() {}

    public Long getId() { return id; }
    public String getNomeArquivo() { return nomeArquivo; }
    public void setNomeArquivo(String nomeArquivo) { this.nomeArquivo = nomeArquivo; }
    public LocalDateTime getDataImportacao() { return dataImportacao; }
    public void setDataImportacao(LocalDateTime dataImportacao) { this.dataImportacao = dataImportacao; }
    public String getOperador() { return operador; }
    public void setOperador(String operador) { this.operador = operador; }
    public Integer getTotalLinhas() { return totalLinhas; }
    public void setTotalLinhas(Integer totalLinhas) { this.totalLinhas = totalLinhas; }
    public Integer getProcessadas() { return processadas; }
    public void setProcessadas(Integer processadas) { this.processadas = processadas; }
    public Integer getBloqueadas() { return bloqueadas; }
    public void setBloqueadas(Integer bloqueadas) { this.bloqueadas = bloqueadas; }
    public List<GlosaItem> getItens() { return itens; }
    public void setItens(List<GlosaItem> itens) { this.itens = itens; }
}
