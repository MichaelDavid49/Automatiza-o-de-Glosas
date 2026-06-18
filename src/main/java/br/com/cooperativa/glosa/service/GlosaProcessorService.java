package br.com.cooperativa.glosa.service;

import br.com.cooperativa.glosa.domain.GlosaImportacao;
import br.com.cooperativa.glosa.domain.GlosaItem;
import br.com.cooperativa.glosa.domain.TipoGlosa;
import br.com.cooperativa.glosa.dto.GlosaItemDTO;
import br.com.cooperativa.glosa.factory.GlosaItemFactory;
import br.com.cooperativa.glosa.strategy.PercentualCalculador;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class GlosaProcessorService {

    private static final Logger log = LoggerFactory.getLogger(GlosaProcessorService.class);

    private final PercentualCalculador calculador;
    private final GlosaItemFactory factory;

    public GlosaProcessorService(PercentualCalculador calculador, GlosaItemFactory factory) {
        this.calculador = calculador;
        this.factory = factory;
    }

    public GlosaItem processar(GlosaItemDTO dto, GlosaImportacao importacao) {
        String erro = validar(dto);
        if (erro != null) {
            log.warn("Bloqueado [{}]: {}", dto.guiaSenha(), erro);
            return factory.criarBloqueado(dto, erro, importacao);
        }
        double percentual = calculador.calcular(dto);
        TipoGlosa tipo = percentual >= 100.0 ? TipoGlosa.TOTAL : TipoGlosa.PARCIAL;
        log.debug("Processado [{}]: {}% - {}", dto.guiaSenha(), String.format("%.2f", percentual), tipo);
        return factory.criarProcessado(dto, percentual, tipo, importacao);
    }

    private String validar(GlosaItemDTO dto) {
        if (dto.guiaSenha() == null || dto.guiaSenha().isBlank())
            return "Guia/Senha obrigatoria (coluna A)";
        if (dto.procedimento() == null || dto.procedimento().isBlank())
            return "Procedimento obrigatorio (coluna C)";
        if (dto.dataRealizacao() == null)
            return "Data obrigatoria (coluna B)";
        if (dto.valorInformado() == null || dto.valorInformado().compareTo(BigDecimal.ZERO) == 0)
            return "Valor informado nao pode ser zero (coluna D)";
        if (dto.valorInformado().compareTo(BigDecimal.ZERO) < 0)
            return "Valor informado negativo (coluna D)";
        if (dto.valorGlosa() != null && dto.valorGlosa().compareTo(BigDecimal.ZERO) < 0)
            return "Valor de glosa negativo (coluna F)";
        if (dto.valorPago() != null && dto.valorPago().compareTo(BigDecimal.ZERO) < 0)
            return "Valor pago negativo (coluna E)";
        if (dto.valorGlosa() != null && dto.valorGlosa().compareTo(dto.valorInformado()) > 0)
            return "Valor glosado maior que valor informado";
        return null;
    }
}
