package br.com.cooperativa.glosa.controller;

import br.com.cooperativa.glosa.dto.IndicadoresDTO;
import br.com.cooperativa.glosa.service.IndicadorService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/indicadores")
public class IndicadorController {

    private final IndicadorService indicadorService;

    public IndicadorController(IndicadorService indicadorService) {
        this.indicadorService = indicadorService;
    }

    @GetMapping
    public String dashboard(Model model) {
        model.addAttribute("ind", indicadorService.calcular());
        return "indicadores";
    }

    @GetMapping("/api")
    @ResponseBody
    public ResponseEntity<IndicadoresDTO> api() {
        return ResponseEntity.ok(indicadorService.calcular());
    }
}
