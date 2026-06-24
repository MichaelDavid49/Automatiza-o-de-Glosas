package br.com.cooperativa.glosa.dto;
import java.util.List;
public record IndicadoresDTO(
    int totalImportacoes, int totalProcedimentos,
    int totalProcessados, int totalBloqueados,
    double percentualSucesso,
    double valorTotalInformado, double valorTotalGlosa, double valorTotalPago,
    List<IndicadorPeriodoDTO> porPeriodo,
    List<IndicadorMotivoDTO> motivosFrequentes) {}
