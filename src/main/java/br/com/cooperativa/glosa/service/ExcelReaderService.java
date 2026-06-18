package br.com.cooperativa.glosa.service;

import br.com.cooperativa.glosa.dto.GlosaItemDTO;
import br.com.cooperativa.glosa.exception.ArquivoInvalidoException;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Service
public class ExcelReaderService {

    private static final Logger log = LoggerFactory.getLogger(ExcelReaderService.class);

    public List<GlosaItemDTO> lerArquivo(MultipartFile file) {
        validarArquivo(file);
        try (Workbook wb = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = wb.getSheetAt(0);
            Row header = sheet.getRow(0);
            if (header == null || header.getLastCellNum() < 7)
                throw new ArquivoInvalidoException("O arquivo deve ter as colunas A a G");
            List<GlosaItemDTO> lista = new ArrayList<>();
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                try {
                    lista.add(mapear(row));
                } catch (Exception e) {
                    log.warn("Linha {} ignorada: {}", i + 1, e.getMessage());
                }
            }
            return lista;
        } catch (ArquivoInvalidoException e) {
            throw e;
        } catch (IOException e) {
            throw new ArquivoInvalidoException("Erro ao abrir o arquivo: " + e.getMessage(), e);
        }
    }

    private void validarArquivo(MultipartFile file) {
        if (file == null || file.isEmpty())
            throw new ArquivoInvalidoException("Arquivo nao pode ser vazio");
        String nome = file.getOriginalFilename();
        if (nome == null || (!nome.endsWith(".xlsx") && !nome.endsWith(".xls")))
            throw new ArquivoInvalidoException("Apenas arquivos .xlsx e .xls sao aceitos");
    }

    private GlosaItemDTO mapear(Row row) {
        return new GlosaItemDTO(
            texto(row, 0), data(row, 1), texto(row, 2),
            decimal(row, 3), decimal(row, 4), decimal(row, 5), texto(row, 6)
        );
    }

    private String texto(Row row, int col) {
        Cell c = row.getCell(col, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        if (c == null) return "";
        if (c.getCellType() == CellType.NUMERIC) return String.valueOf((long) c.getNumericCellValue());
        return c.getStringCellValue().trim();
    }

    private BigDecimal decimal(Row row, int col) {
        Cell c = row.getCell(col, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        if (c == null) return BigDecimal.ZERO;
        try {
            return BigDecimal.valueOf(c.getNumericCellValue()).setScale(2, RoundingMode.HALF_UP);
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    private LocalDate data(Row row, int col) {
        Cell c = row.getCell(col, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        if (c == null) return null;
        try {
            return c.getDateCellValue().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        } catch (Exception e) {
            return null;
        }
    }
}
