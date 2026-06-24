package br.com.cooperativa.glosa.service;

import br.com.cooperativa.glosa.domain.GlosaImportacao;
import br.com.cooperativa.glosa.dto.GlosaItemDTO;
import br.com.cooperativa.glosa.dto.ImportacaoResultDTO;
import br.com.cooperativa.glosa.exception.ArquivoInvalidoException;
import br.com.cooperativa.glosa.factory.GlosaItemFactory;
import br.com.cooperativa.glosa.repository.GlosaImportacaoRepository;
import br.com.cooperativa.glosa.repository.GlosaItemRepository;
import br.com.cooperativa.glosa.strategy.PercentualPorGlosadoStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("GlosaImportacaoService - Fluxo completo de importacao")
class GlosaImportacaoServiceTest {

    @Mock ExcelReaderService excelReader;
    @Mock GlosaImportacaoRepository importacaoRepo;
    @Mock GlosaItemRepository itemRepo;

    private GlosaImportacaoService service;

    private static final MockMultipartFile ARQUIVO = new MockMultipartFile(
        "arquivo", "glosas.xlsx",
        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
        new byte[]{1, 2, 3}
    );

    @BeforeEach
    void setUp() {
        GlosaProcessorService processor = new GlosaProcessorService(
            new PercentualPorGlosadoStrategy(),
            new GlosaItemFactory()
        );
        service = new GlosaImportacaoService(excelReader, processor, importacaoRepo, itemRepo);
    }

    private GlosaItemDTO dto(String guia, String inf, String pago, String glosa) {
        return new GlosaItemDTO(guia, LocalDate.of(2025, 1, 10), "Consulta medica",
            new BigDecimal(inf), new BigDecimal(pago), new BigDecimal(glosa), "Motivo glosa");
    }

    @Test
    @DisplayName("RF01/RF03/RF04 - Glosa total processada com sucesso")
    void glosaTotal() {
        when(importacaoRepo.save(any())).thenAnswer(i -> i.getArgument(0));
        when(excelReader.lerArquivo(any())).thenReturn(List.of(dto("G-001", "500", "0", "500")));
        ImportacaoResultDTO r = service.importar(ARQUIVO, "operador");
        assertThat(r.processadas()).isEqualTo(1);
        assertThat(r.bloqueadas()).isEqualTo(0);
        assertThat(r.totalLinhas()).isEqualTo(1);
    }

    @Test
    @DisplayName("RF01/RF03/RF04 - Glosa parcial processada com sucesso")
    void glosaParcial() {
        when(importacaoRepo.save(any())).thenAnswer(i -> i.getArgument(0));
        when(excelReader.lerArquivo(any())).thenReturn(List.of(dto("G-002", "500", "300", "200")));
        ImportacaoResultDTO r = service.importar(ARQUIVO, "operador");
        assertThat(r.processadas()).isEqualTo(1);
        assertThat(r.bloqueadas()).isEqualTo(0);
    }

    @Test
    @DisplayName("RF07/RN06 - Valores inconsistentes: item bloqueado individualmente")
    void valoresInconsistentes() {
        when(importacaoRepo.save(any())).thenAnswer(i -> i.getArgument(0));
        when(excelReader.lerArquivo(any())).thenReturn(List.of(dto("G-003", "500", "0", "700")));
        ImportacaoResultDTO r = service.importar(ARQUIVO, "operador");
        assertThat(r.bloqueadas()).isEqualTo(1);
        assertThat(r.processadas()).isEqualTo(0);
        assertThat(r.itens().get(0).motivoBloqueio()).isNotBlank();
    }

    @Test
    @DisplayName("RF07/RN06 - Valor total zero: item bloqueado")
    void valorTotalZero() {
        when(importacaoRepo.save(any())).thenAnswer(i -> i.getArgument(0));
        when(excelReader.lerArquivo(any())).thenReturn(List.of(dto("G-004", "0", "0", "0")));
        ImportacaoResultDTO r = service.importar(ARQUIVO, "operador");
        assertThat(r.bloqueadas()).isEqualTo(1);
        assertThat(r.processadas()).isEqualTo(0);
    }

    @Test
    @DisplayName("RN07 - Item invalido nao impede os demais de serem processados")
    void itemInvalidoNaoImpedeDemais() {
        when(importacaoRepo.save(any())).thenAnswer(i -> i.getArgument(0));
        when(excelReader.lerArquivo(any())).thenReturn(List.of(
            dto("G-005", "0",   "0",   "0"  ),
            dto("G-006", "500", "300", "200")
        ));
        ImportacaoResultDTO r = service.importar(ARQUIVO, "operador");
        assertThat(r.processadas()).isEqualTo(1);
        assertThat(r.bloqueadas()).isEqualTo(1);
        assertThat(r.totalLinhas()).isEqualTo(2);
    }

    @Test
    @DisplayName("RF02 - Arquivo invalido: lanca ArquivoInvalidoException sem salvar")
    void arquivoInvalido() {
        when(excelReader.lerArquivo(any()))
            .thenThrow(new ArquivoInvalidoException("Formato invalido"));
        assertThatThrownBy(() -> service.importar(ARQUIVO, "operador"))
            .isInstanceOf(ArquivoInvalidoException.class)
            .hasMessageContaining("Formato invalido");
        verify(importacaoRepo, never()).save(any());
    }
}
