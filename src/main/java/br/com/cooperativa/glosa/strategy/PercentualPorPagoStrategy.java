package br.com.cooperativa.glosa.strategy;
import br.com.cooperativa.glosa.dto.GlosaItemDTO;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.math.RoundingMode;
@Component
public class PercentualPorPagoStrategy implements PercentualCalculador {
    @Override
    public double calcular(GlosaItemDTO dto) {
        if (dto.valorInformado() == null || dto.valorInformado().compareTo(BigDecimal.ZERO) == 0)
            return 0.0;
        BigDecimal pago = dto.valorPago() != null ? dto.valorPago() : BigDecimal.ZERO;
        return dto.valorInformado().subtract(pago)
            .divide(dto.valorInformado(), 6, RoundingMode.HALF_UP)
            .multiply(BigDecimal.valueOf(100)).doubleValue();
    }
}
