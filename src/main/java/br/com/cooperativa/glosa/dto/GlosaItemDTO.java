package br.com.cooperativa.glosa.dto;
import java.math.BigDecimal;
import java.time.LocalDate;
public record GlosaItemDTO(
    String guiaSenha,
    LocalDate dataRealizacao,
    String procedimento,
    BigDecimal valorInformado,
    BigDecimal valorPago,
    BigDecimal valorGlosa,
    String motivoGlosa
) {}
