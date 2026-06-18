package br.com.cooperativa.glosa.strategy;

import br.com.cooperativa.glosa.dto.GlosaItemDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDate;
import static org.assertj.core.api.Assertions.*;

@DisplayName("PercentualPorGlosadoStrategy - RN01, RN02, RN03")
class PercentualPorGlosadoStrategyTest {

    private final PercentualPorGlosadoStrategy strategy = new PercentualPorGlosadoStrategy();

    private GlosaItemDTO dto(String inf, String pago, String glosa) {
        return new GlosaItemDTO("G1", LocalDate.of(2025, 1, 10), "CONSULTA",
            new BigDecimal(inf), new BigDecimal(pago), new BigDecimal(glosa), "Motivo teste");
    }

    @Test
    @DisplayName("RN02 - Glosa total: percentual = 100 quando valor glosado = valor informado")
    void glosaTotal100Porcento() {
        assertThat(strategy.calcular(dto("500", "0", "500"))).isEqualTo(100.0);
    }

    @Test
    @DisplayName("RN03 - Glosa parcial: percentual 40 quando glosa = 200 de 500")
    void glosaParcial40Porcento() {
        assertThat(strategy.calcular(dto("500", "300", "200"))).isCloseTo(40.0, within(0.01));
    }

    @Test
    @DisplayName("Sem glosa: percentual 0 quando valor glosado = 0")
    void semGlosa() {
        assertThat(strategy.calcular(dto("500", "500", "0"))).isEqualTo(0.0);
    }

    @Test
    @DisplayName("RN06 - Valor informado zero: retorna 0 (evita divisao por zero)")
    void valorInformadoZeroNaoDivide() {
        assertThat(strategy.calcular(dto("0", "0", "0"))).isEqualTo(0.0);
    }

    @Test
    @DisplayName("Valor informado nulo: retorna 0")
    void valorInformadoNulo() {
        GlosaItemDTO d = new GlosaItemDTO("G1", LocalDate.now(), "P",
            null, BigDecimal.ZERO, BigDecimal.ZERO, "m");
        assertThat(strategy.calcular(d)).isEqualTo(0.0);
    }
}
