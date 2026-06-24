package br.com.cooperativa.glosa.factory;
import br.com.cooperativa.glosa.domain.GlosaImportacao;
import br.com.cooperativa.glosa.domain.GlosaItem;
import br.com.cooperativa.glosa.domain.SituacaoGlosa;
import br.com.cooperativa.glosa.domain.TipoGlosa;
import br.com.cooperativa.glosa.dto.GlosaItemDTO;
import org.springframework.stereotype.Component;
@Component
public class GlosaItemFactory {
    public GlosaItem criarBloqueado(GlosaItemDTO dto, String motivo, GlosaImportacao imp) {
        GlosaItem item = new GlosaItem();
        item.setImportacao(imp);
        item.setGuiaSenha(dto.guiaSenha());
        item.setDataRealizacao(dto.dataRealizacao());
        item.setProcedimento(dto.procedimento());
        item.setValorInformado(dto.valorInformado());
        item.setValorPago(dto.valorPago());
        item.setValorGlosa(dto.valorGlosa());
        item.setSituacao(SituacaoGlosa.BLOQUEADA);
        item.setMotivoBloqueio(motivo);
        return item;
    }
    public GlosaItem criarProcessado(GlosaItemDTO dto, double percentual, TipoGlosa tipo, GlosaImportacao imp) {
        GlosaItem item = new GlosaItem();
        item.setImportacao(imp);
        item.setGuiaSenha(dto.guiaSenha());
        item.setDataRealizacao(dto.dataRealizacao());
        item.setProcedimento(dto.procedimento());
        item.setValorInformado(dto.valorInformado());
        item.setValorPago(dto.valorPago());
        item.setValorGlosa(dto.valorGlosa());
        item.setPercentualGlosa(percentual);
        item.setTipoGlosa(tipo);
        item.setSituacao(SituacaoGlosa.SERA_REAPRESENTADA);
        item.setMotivoGlosa(dto.motivoGlosa());
        return item;
    }
}
