package br.com.cooperativa.glosa.service;

import br.com.cooperativa.glosa.domain.GlosaImportacao;
import br.com.cooperativa.glosa.domain.GlosaItem;
import br.com.cooperativa.glosa.dto.GlosaItemDTO;
import br.com.cooperativa.glosa.dto.ImportacaoResultDTO;
import br.com.cooperativa.glosa.repository.GlosaImportacaoRepository;
import br.com.cooperativa.glosa.repository.GlosaItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class GlosaImportacaoService {

    private static final Logger log = LoggerFactory.getLogger(GlosaImportacaoService.class);

    private final ExcelReaderService excelReader;
    private final GlosaProcessorService processor;
    private final GlosaImportacaoRepository importacaoRepo;
    private final GlosaItemRepository itemRepo;

    public GlosaImportacaoService(ExcelReaderService excelReader,
                                   GlosaProcessorService processor,
                                   GlosaImportacaoRepository importacaoRepo,
                                   GlosaItemRepository itemRepo) {
        this.excelReader = excelReader;
        this.processor = processor;
        this.importacaoRepo = importacaoRepo;
        this.itemRepo = itemRepo;
    }

    @Transactional
    public ImportacaoResultDTO importar(MultipartFile file, String operador) {
        log.info("Importando arquivo: {}", file.getOriginalFilename());

        List<GlosaItemDTO> dtos = excelReader.lerArquivo(file);

        GlosaImportacao importacao = new GlosaImportacao();
        importacao.setNomeArquivo(file.getOriginalFilename());
        importacao.setDataImportacao(LocalDateTime.now());
        importacao.setOperador(operador);
        importacao.setTotalLinhas(dtos.size());
        importacao.setProcessadas(0);
        importacao.setBloqueadas(0);
        importacaoRepo.save(importacao);

        List<GlosaItem> itens = dtos.stream()
            .map(dto -> processor.processar(dto, importacao))
            .toList();
        itemRepo.saveAll(itens);

        long bloqueadas = itens.stream().filter(GlosaItem::estaBloqueado).count();
        importacao.setProcessadas((int)(itens.size() - bloqueadas));
        importacao.setBloqueadas((int) bloqueadas);
        importacaoRepo.save(importacao);

        log.info("Concluido: {} processadas, {} bloqueadas", importacao.getProcessadas(), importacao.getBloqueadas());
        return ImportacaoResultDTO.from(importacao, itens);
    }

    public List<ImportacaoResultDTO> historico() {
        return importacaoRepo.findAllByOrderByDataImportacaoDesc().stream()
            .map(imp -> ImportacaoResultDTO.from(imp, itemRepo.findByImportacaoId(imp.getId())))
            .toList();
    }

    public ImportacaoResultDTO buscarPorId(Long id) {
        GlosaImportacao imp = importacaoRepo.findById(id)
            .orElseThrow(() -> new RuntimeException("Importacao nao encontrada: " + id));
        return ImportacaoResultDTO.from(imp, itemRepo.findByImportacaoId(id));
    }
}
