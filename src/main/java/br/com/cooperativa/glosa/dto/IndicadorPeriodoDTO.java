package br.com.cooperativa.glosa.dto;
public record IndicadorPeriodoDTO(
    String periodo, int total, int processadas, int bloqueadas,
    double valorInformado, double valorGlosa, double valorPago) {}
