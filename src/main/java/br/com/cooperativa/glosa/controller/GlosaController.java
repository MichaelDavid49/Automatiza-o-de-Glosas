package br.com.cooperativa.glosa.controller;

import br.com.cooperativa.glosa.dto.ImportacaoResultDTO;
import br.com.cooperativa.glosa.service.GlosaImportacaoService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
public class GlosaController {

    private final GlosaImportacaoService service;

    public GlosaController(GlosaImportacaoService service) {
        this.service = service;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("historico", service.historico());
        return "index";
    }

    @PostMapping("/importar")
    public String importar(@RequestParam("arquivo") MultipartFile arquivo,
                           @RequestParam(defaultValue = "operador") String operador,
                           Model model) {
        try {
            ImportacaoResultDTO resultado = service.importar(arquivo, operador);
            model.addAttribute("resultado", resultado);
            model.addAttribute("historico", service.historico());
            return "index";
        } catch (Exception e) {
            model.addAttribute("erro", e.getMessage());
            model.addAttribute("historico", service.historico());
            return "index";
        }
    }

    @GetMapping("/importacao/{id}")
    public String detalhe(@PathVariable Long id, Model model) {
        model.addAttribute("resultado", service.buscarPorId(id));
        model.addAttribute("historico", service.historico());
        return "index";
    }

    @GetMapping("/api/importar")
    @ResponseBody
    public ResponseEntity<List<ImportacaoResultDTO>> historicoApi() {
        return ResponseEntity.ok(service.historico());
    }
}
