package br.com.senai.bebidas.controller;

import br.com.senai.bebidas.dto.request.MovimentacaoCreateRequest;
import br.com.senai.bebidas.dto.response.MovimentacaoResponse;
import br.com.senai.bebidas.service.MovimentacaoService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/movimentacoes")
public class MovimentacaoController {

    private final MovimentacaoService movimentacaoService;

    public MovimentacaoController(MovimentacaoService movimentacaoService) {
        this.movimentacaoService = movimentacaoService;
    }

    @PostMapping
    public ResponseEntity<MovimentacaoResponse> registrar(@Valid @RequestBody MovimentacaoCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(movimentacaoService.registrar(request));
    }

    @GetMapping
    public ResponseEntity<List<MovimentacaoResponse>> listar(
            @RequestParam(name = "insumoId", required = false) Long insumoId,
            @RequestParam(name = "setorId", required = false) Long setorId,
            @RequestParam(name = "usuarioId", required = false) Long usuarioId,
            @RequestParam(name = "tipo", required = false) String tipo,
            @RequestParam(name = "dataInicio", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(name = "dataFim", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {
        return ResponseEntity.ok(movimentacaoService.listar(insumoId, setorId, usuarioId, tipo, dataInicio, dataFim));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MovimentacaoResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(movimentacaoService.buscarPorId(id));
    }
}
