package br.com.cooperativa.glosa.factory;

import br.com.cooperativa.glosa.domain.GlosaItem;
import br.com.cooperativa.glosa.domain.SituacaoGlosa;
import br.com.cooperativa.glosa.domain.TipoGlosa;
import br.com.cooperativa.glosa.dto.GlosaItemDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDate;
import static org.assertj.core.api.Assertions.*;

@DisplayName("GlosaItemFactory - Factory Method Pattern")
class GlosaItemFactoryTest {

    private final GlosaItemFactory factory = new GlosaItemFactory();

    private final GlosaItemDTO dto = new GlosaItemDTO(
        "G-001", LocalDate.of(2025, 1, 10), "Consulta cardiologia",
        new BigDecimal("500.00"), new BigDecimal("300.00"), new BigDecimal("200.00"), "Codigo divergente"
    );

    @Test
    @DisplayName("criarBloqueado: situacao deve ser BLOQUEADA com motivo registrado")
    void criarBloqueadoDeveTerSituacaoBloqueada() {
        GlosaItem item = factory.criarBloqueado(dto, "Valor informado zero", null);
        assertThat(item.getSituacao()).isEqualTo(SituacaoGlosa.BLOQUEADA);
        assertThat(item.getMotivoBloqueio()).isEqualTo("Valor informado zero");
        assertThat(item.getTipoGlosa()).isNull();
    }

    @Test
    @DisplayName("criarBloqueado: dados originais do DTO devem ser preservados")
    void criarBloqueadoDevePreservarDados() {
        GlosaItem item = factory.criarBloqueado(dto, "erro", null);
        assertThat(item.getGuiaSenha()).isEqualTo("G-001");
        assertThat(item.getProcedimento()).isEqualTo("Consulta cardiologia");
        assertThat(item.getValorInformado()).isEqualByComparingTo("500.00");
    }

    @Test
    @DisplayName("criarProcessado: situacao deve ser SERA_REAPRESENTADA (RN04)")
    void criarProcessadoDeveTerSituacaoSeraReapresentada() {
        GlosaItem item = factory.criarProcessado(dto, 40.0, TipoGlosa.PARCIAL, null);
        assertThat(item.getSituacao()).isEqualTo(SituacaoGlosa.SERA_REAPRESENTADA);
    }

    @Test
    @DisplayName("criarProcessado: tipo PARCIAL quando percentual < 100 (RN03)")
    void criarProcessadoComTipoParcial() {
        GlosaItem item = factory.criarProcessado(dto, 40.0, TipoGlosa.PARCIAL, null);
        assertThat(item.getTipoGlosa()).isEqualTo(TipoGlosa.PARCIAL);
        assertThat(item.getPercentualGlosa()).isCloseTo(40.0, within(0.01));
    }

    @Test
    @DisplayName("criarProcessado: tipo TOTAL quando percentual = 100 (RN02)")
    void criarProcessadoComTipoTotal() {
        GlosaItemDTO dtoTotal = new GlosaItemDTO("G-002", LocalDate.now(), "Procedimento",
            new BigDecimal("500"), BigDecimal.ZERO, new BigDecimal("500"), "Nao autorizado");
        GlosaItem item = factory.criarProcessado(dtoTotal, 100.0, TipoGlosa.TOTAL, null);
        assertThat(item.getTipoGlosa()).isEqualTo(TipoGlosa.TOTAL);
    }

    @Test
    @DisplayName("criarProcessado: motivo da glosa extraido da coluna G (RN05)")
    void criarProcessadoDeveTerMotivoDoArquivo() {
        GlosaItem item = factory.criarProcessado(dto, 40.0, TipoGlosa.PARCIAL, null);
        assertThat(item.getMotivoGlosa()).isEqualTo("Codigo divergente");
    }
}
