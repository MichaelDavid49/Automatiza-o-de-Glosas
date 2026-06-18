package br.com.cooperativa.glosa.strategy;
import br.com.cooperativa.glosa.dto.GlosaItemDTO;
public interface PercentualCalculador {
    double calcular(GlosaItemDTO dto);
}
