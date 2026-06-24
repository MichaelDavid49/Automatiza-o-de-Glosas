package br.com.cooperativa.glosa.service;

import br.com.cooperativa.glosa.domain.GlosaItem;
import br.com.cooperativa.glosa.domain.SituacaoGlosa;
import br.com.cooperativa.glosa.dto.IndicadoresDTO;
import br.com.cooperativa.glosa.repository.GlosaImportacaoRepository;
import br.com.cooperativa.glosa.repository.GlosaItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("IndicadorService")
class IndicadorServiceTest {

    @Mock GlosaImportacaoRepository importacaoRepo;
    @Mock GlosaItemRepository itemRepo;

    private IndicadorService service;

    @BeforeEach
    void setUp() {
        service = new IndicadorService(importacaoRepo, itemRepo);
    }

    private GlosaItem item(String guia, boolean bloqueado, String motivo,
                            double inf, double glosa, double pago, LocalDate data) {
        GlosaItem i = new GlosaItem();
        i.setGuiaSenha(guia);
        i.setSituacao(bloqueado ? SituacaoGlosa.BLOQUEADA : SituacaoGlosa.SERA_REAPRESENTADA);
        i.setMotivoGlosa(motivo);
        i.setValorInformado(BigDecimal.valueOf(inf));
        i.setValorGlosa(BigDecimal.valueOf(glosa));
        i.setValorPago(BigDecimal.valueOf(pago));
        i.setDataRealizacao(data);
        return i;
    }

    @Test
    @DisplayName("Contadores corretos: processados, bloqueados e percentual de sucesso")
    void contadoresCorretos() {
        when(importacaoRepo.count()).thenReturn(2L);
        when(itemRepo.findAll()).thenReturn(List.of(
            item("G1", false, "Motivo A", 500, 200, 300, LocalDate.of(2025, 1, 10)),
            item("G2", false, "Motivo A", 500, 200, 300, LocalDate.of(2025, 1, 10)),
            item("G3", true,  null,        0,   0,   0, LocalDate.of(2025, 1, 10))
        ));

        IndicadoresDTO ind = service.calcular();

        assertThat(ind.totalProcedimentos()).isEqualTo(3);
        assertThat(ind.totalProcessados()).isEqualTo(2);
        assertThat(ind.totalBloqueados()).isEqualTo(1);
        assertThat(ind.totalImportacoes()).isEqualTo(2);
        assertThat(ind.percentualSucesso()).isCloseTo(66.66, within(0.01));
    }

    @Test
    @DisplayName("Soma dos valores financeiros esta correta")
    void valoresFinanceirosCorretos() {
        when(importacaoRepo.count()).thenReturn(1L);
        when(itemRepo.findAll()).thenReturn(List.of(
            item("G1", false, "M", 1000, 400, 600, LocalDate.of(2025, 2, 1)),
            item("G2", false, "M",  500, 100, 400, LocalDate.of(2025, 2, 1))
        ));

        IndicadoresDTO ind = service.calcular();

        assertThat(ind.valorTotalInformado()).isEqualTo(1500.0);
        assertThat(ind.valorTotalGlosa()).isEqualTo(500.0);
        assertThat(ind.valorTotalPago()).isEqualTo(1000.0);
    }

    @Test
    @DisplayName("Agrupamento por periodo correto (MM/yyyy)")
    void agrupamentoPorPeriodo() {
        when(importacaoRepo.count()).thenReturn(1L);
        when(itemRepo.findAll()).thenReturn(List.of(
            item("G1", false, "M", 500, 200, 300, LocalDate.of(2025, 1, 10)),
            item("G2", false, "M", 500, 200, 300, LocalDate.of(2025, 1, 20)),
            item("G3", false, "M", 500, 200, 300, LocalDate.of(2025, 2, 5))
        ));

        IndicadoresDTO ind = service.calcular();

        assertThat(ind.porPeriodo()).hasSize(2);
        assertThat(ind.porPeriodo().get(0).periodo()).isEqualTo("01/2025");
        assertThat(ind.porPeriodo().get(0).total()).isEqualTo(2);
        assertThat(ind.porPeriodo().get(1).periodo()).isEqualTo("02/2025");
        assertThat(ind.porPeriodo().get(1).total()).isEqualTo(1);
    }

    @Test
    @DisplayName("Ranking de motivos: ordenado por frequencia decrescente, limite 10")
    void rankingMotivos() {
        when(importacaoRepo.count()).thenReturn(1L);
        LocalDate d = LocalDate.of(2025, 1, 10);
        when(itemRepo.findAll()).thenReturn(List.of(
            item("G1", false, "Codigo divergente",  500, 200, 300, d),
            item("G2", false, "Codigo divergente",  500, 200, 300, d),
            item("G3", false, "Codigo divergente",  500, 200, 300, d),
            item("G4", false, "Laudo ausente",       500, 200, 300, d),
            item("G5", false, "Laudo ausente",       500, 200, 300, d),
            item("G6", false, "Nao autorizado",      500, 200, 300, d)
        ));

        IndicadoresDTO ind = service.calcular();

        assertThat(ind.motivosFrequentes()).hasSize(3);
        assertThat(ind.motivosFrequentes().get(0).motivo()).isEqualTo("Codigo divergente");
        assertThat(ind.motivosFrequentes().get(0).quantidade()).isEqualTo(3);
        assertThat(ind.motivosFrequentes().get(1).motivo()).isEqualTo("Laudo ausente");
    }

    @Test
    @DisplayName("Itens sem motivo de glosa nao aparecem no ranking")
    void itensSemMotivoIgnoradosNoRanking() {
        when(importacaoRepo.count()).thenReturn(1L);
        LocalDate d = LocalDate.of(2025, 1, 10);
        when(itemRepo.findAll()).thenReturn(List.of(
            item("G1", false, "Motivo valido", 500, 200, 300, d),
            item("G2", true,  null,             0,   0,   0, d),
            item("G3", true,  "",               0,   0,   0, d)
        ));

        IndicadoresDTO ind = service.calcular();

        assertThat(ind.motivosFrequentes()).hasSize(1);
        assertThat(ind.motivosFrequentes().get(0).motivo()).isEqualTo("Motivo valido");
    }

    @Test
    @DisplayName("Sem dados: retorna zeros sem lancar excecao")
    void semDadosRetornaZeros() {
        when(importacaoRepo.count()).thenReturn(0L);
        when(itemRepo.findAll()).thenReturn(List.of());

        IndicadoresDTO ind = service.calcular();

        assertThat(ind.totalProcedimentos()).isEqualTo(0);
        assertThat(ind.percentualSucesso()).isEqualTo(0.0);
        assertThat(ind.valorTotalGlosa()).isEqualTo(0.0);
        assertThat(ind.porPeriodo()).isEmpty();
        assertThat(ind.motivosFrequentes()).isEmpty();
    }
}
