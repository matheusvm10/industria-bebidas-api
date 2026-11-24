package br.com.senai.bebidas.controller;

import br.com.senai.bebidas.dto.response.AlertaResponse;
import br.com.senai.bebidas.service.InsumoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/alertas")
public class AlertaController {

    private final InsumoService insumoService;

    public AlertaController(InsumoService insumoService) {
        this.insumoService = insumoService;
    }

    @GetMapping("/estoque")
    public ResponseEntity<List<AlertaResponse>> estoqueCritico() {
        return ResponseEntity.ok(insumoService.listarAlertas());
    }
}
