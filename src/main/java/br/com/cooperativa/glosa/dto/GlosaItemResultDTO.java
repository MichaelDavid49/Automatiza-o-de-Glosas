package br.com.cooperativa.glosa.dto;
import br.com.cooperativa.glosa.domain.GlosaItem;
import br.com.cooperativa.glosa.domain.SituacaoGlosa;
import br.com.cooperativa.glosa.domain.TipoGlosa;
import java.math.BigDecimal;
import java.time.LocalDate;
public record GlosaItemResultDTO(
    Long id, String guiaSenha, LocalDate dataRealizacao, String procedimento,
    BigDecimal valorInformado, BigDecimal valorPago, BigDecimal valorGlosa,
    Double percentualGlosa, TipoGlosa tipoGlosa, SituacaoGlosa situacao,
    String motivoGlosa, String motivoBloqueio
) {
    public static GlosaItemResultDTO from(GlosaItem i) {
        return new GlosaItemResultDTO(i.getId(), i.getGuiaSenha(), i.getDataRealizacao(),
            i.getProcedimento(), i.getValorInformado(), i.getValorPago(), i.getValorGlosa(),
            i.getPercentualGlosa(), i.getTipoGlosa(), i.getSituacao(),
            i.getMotivoGlosa(), i.getMotivoBloqueio());
    }
}
