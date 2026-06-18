package br.com.cooperativa.glosa.dto;
import br.com.cooperativa.glosa.domain.GlosaImportacao;
import br.com.cooperativa.glosa.domain.GlosaItem;
import java.time.LocalDateTime;
import java.util.List;
public record ImportacaoResultDTO(
    Long id, String nomeArquivo, LocalDateTime dataImportacao, String operador,
    int totalLinhas, int processadas, int bloqueadas,
    List<GlosaItemResultDTO> itens
) {
    public static ImportacaoResultDTO from(GlosaImportacao imp, List<GlosaItem> itens) {
        return new ImportacaoResultDTO(
            imp.getId(), imp.getNomeArquivo(), imp.getDataImportacao(), imp.getOperador(),
            imp.getTotalLinhas() != null ? imp.getTotalLinhas() : 0,
            imp.getProcessadas() != null ? imp.getProcessadas() : 0,
            imp.getBloqueadas() != null ? imp.getBloqueadas() : 0,
            itens.stream().map(GlosaItemResultDTO::from).toList()
        );
    }
}
