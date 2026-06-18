package br.com.cooperativa.glosa.service;

import br.com.cooperativa.glosa.domain.GlosaItem;
import br.com.cooperativa.glosa.domain.SituacaoGlosa;
import br.com.cooperativa.glosa.domain.TipoGlosa;
import br.com.cooperativa.glosa.dto.GlosaItemDTO;
import br.com.cooperativa.glosa.factory.GlosaItemFactory;
import br.com.cooperativa.glosa.strategy.PercentualPorGlosadoStrategy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDate;
import static org.assertj.core.api.Assertions.*;

@DisplayName("GlosaProcessorService - Regras de Negocio RN01 a RN07")
class GlosaProcessorServiceTest {

    private final GlosaProcessorService service = new GlosaProcessorService(
        new PercentualPorGlosadoStrategy(),
        new GlosaItemFactory()
    );

    private GlosaItemDTO dto(String guia, String inf, String pago, String glosa) {
        return new GlosaItemDTO(guia, LocalDate.of(2025, 1, 10), "Consulta medica",
            new BigDecimal(inf), new BigDecimal(pago), new BigDecimal(glosa), "Motivo glosa");
    }

    @Test
    @DisplayName("RN02 - Glosa total: tipo TOTAL e situacao SERA_REAPRESENTADA")
    void glosaTotal() {
        GlosaItem item = service.processar(dto("G-001", "500", "0", "500"), null);
        assertThat(item.getTipoGlosa()).isEqualTo(TipoGlosa.TOTAL);
        assertThat(item.getSituacao()).isEqualTo(SituacaoGlosa.SERA_REAPRESENTADA);
        assertThat(item.getPercentualGlosa()).isEqualTo(100.0);
    }

    @Test
    @DisplayName("RN03 - Glosa parcial: tipo PARCIAL e percentual correto")
    void glosaParcial() {
        GlosaItem item = service.processar(dto("G-002", "500", "300", "200"), null);
        assertThat(item.getTipoGlosa()).isEqualTo(TipoGlosa.PARCIAL);
        assertThat(item.getSituacao()).isEqualTo(SituacaoGlosa.SERA_REAPRESENTADA);
        assertThat(item.getPercentualGlosa()).isCloseTo(40.0, within(0.01));
    }

    @Test
    @DisplayName("RN04 - Situacao sempre SERA_REAPRESENTADA para glosas validas")
    void situacaoSeraReapresentada() {
        GlosaItem item = service.processar(dto("G-003", "1000", "750", "250"), null);
        assertThat(item.getSituacao()).isEqualTo(SituacaoGlosa.SERA_REAPRESENTADA);
    }

    @Test
    @DisplayName("RN06 - Valor total zero: BLOQUEADA com motivo")
    void valorTotalZeroBloqueia() {
        GlosaItem item = service.processar(dto("G-004", "0", "0", "0"), null);
        assertThat(item.getSituacao()).isEqualTo(SituacaoGlosa.BLOQUEADA);
        assertThat(item.getMotivoBloqueio()).isNotBlank();
    }

    @Test
    @DisplayName("RN06 - Valor informado negativo: BLOQUEADA")
    void valorNegativoBloqueia() {
        GlosaItem item = service.processar(dto("G-005", "-100", "0", "100"), null);
        assertThat(item.getSituacao()).isEqualTo(SituacaoGlosa.BLOQUEADA);
    }

    @Test
    @DisplayName("RN06 - Glosa maior que valor informado: BLOQUEADA")
    void glosaMaiorQueInformadoBloqueia() {
        GlosaItem item = service.processar(dto("G-006", "500", "0", "700"), null);
        assertThat(item.getSituacao()).isEqualTo(SituacaoGlosa.BLOQUEADA);
    }

    @Test
    @DisplayName("RN06 - Valor pago negativo: BLOQUEADA")
    void valorPagoNegativoBloqueia() {
        GlosaItem item = service.processar(dto("G-007", "500", "-50", "200"), null);
        assertThat(item.getSituacao()).isEqualTo(SituacaoGlosa.BLOQUEADA);
    }

    @Test
    @DisplayName("RN07 - Item invalido nao impede processamento dos demais")
    void itemInvalidoNaoImpedeDemais() {
        GlosaItem invalido = service.processar(dto("G-008", "0",   "0",   "0"  ), null);
        GlosaItem valido   = service.processar(dto("G-009", "500", "300", "200"), null);
        assertThat(invalido.getSituacao()).isEqualTo(SituacaoGlosa.BLOQUEADA);
        assertThat(valido.getSituacao()).isEqualTo(SituacaoGlosa.SERA_REAPRESENTADA);
    }
}
