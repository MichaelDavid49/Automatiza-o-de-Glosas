package br.com.cooperativa.glosa.service;

import br.com.cooperativa.glosa.domain.GlosaItem;
import br.com.cooperativa.glosa.dto.IndicadorMotivoDTO;
import br.com.cooperativa.glosa.dto.IndicadorPeriodoDTO;
import br.com.cooperativa.glosa.dto.IndicadoresDTO;
import br.com.cooperativa.glosa.repository.GlosaImportacaoRepository;
import br.com.cooperativa.glosa.repository.GlosaItemRepository;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class IndicadorService {

    private final GlosaImportacaoRepository importacaoRepo;
    private final GlosaItemRepository itemRepo;
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("MM/yyyy");

    public IndicadorService(GlosaImportacaoRepository importacaoRepo,
                            GlosaItemRepository itemRepo) {
        this.importacaoRepo = importacaoRepo;
        this.itemRepo = itemRepo;
    }

    public IndicadoresDTO calcular() {
        List<GlosaItem> todos = itemRepo.findAll();
        int total      = todos.size();
        int processados = (int) todos.stream().filter(i -> !i.estaBloqueado()).count();
        int bloqueados  = total - processados;
        int importacoes = (int) importacaoRepo.count();
        double pct      = total == 0 ? 0.0 : (processados * 100.0) / total;

        double vInf  = soma(todos, "informado");
        double vGlosa = soma(todos, "glosa");
        double vPago  = soma(todos, "pago");

        return new IndicadoresDTO(importacoes, total, processados, bloqueados,
            pct, vInf, vGlosa, vPago,
            porPeriodo(todos), motivos(todos));
    }

    private double soma(List<GlosaItem> itens, String campo) {
        return switch (campo) {
            case "informado" -> itens.stream().filter(i -> i.getValorInformado() != null)
                .mapToDouble(i -> i.getValorInformado().doubleValue()).sum();
            case "glosa"     -> itens.stream().filter(i -> i.getValorGlosa() != null)
                .mapToDouble(i -> i.getValorGlosa().doubleValue()).sum();
            default          -> itens.stream().filter(i -> i.getValorPago() != null)
                .mapToDouble(i -> i.getValorPago().doubleValue()).sum();
        };
    }

    private List<IndicadorPeriodoDTO> porPeriodo(List<GlosaItem> itens) {
        return itens.stream().filter(i -> i.getDataRealizacao() != null)
            .collect(Collectors.groupingBy(i -> i.getDataRealizacao().format(FMT),
                TreeMap::new, Collectors.toList()))
            .entrySet().stream().map(e -> {
                List<GlosaItem> l = e.getValue();
                int p = (int) l.stream().filter(i -> !i.estaBloqueado()).count();
                return new IndicadorPeriodoDTO(e.getKey(), l.size(), p, l.size() - p,
                    soma(l,"informado"), soma(l,"glosa"), soma(l,"pago"));
            }).collect(Collectors.toList());
    }

    private List<IndicadorMotivoDTO> motivos(List<GlosaItem> itens) {
        List<GlosaItem> c = itens.stream()
            .filter(i -> i.getMotivoGlosa() != null && !i.getMotivoGlosa().isBlank()).toList();
        long total = c.size();
        return c.stream()
            .collect(Collectors.groupingBy(GlosaItem::getMotivoGlosa, Collectors.counting()))
            .entrySet().stream()
            .sorted(Map.Entry.<String,Long>comparingByValue().reversed())
            .limit(10)
            .map(e -> new IndicadorMotivoDTO(e.getKey(), e.getValue(),
                total == 0 ? 0.0 : (e.getValue() * 100.0) / total))
            .collect(Collectors.toList());
    }
}
