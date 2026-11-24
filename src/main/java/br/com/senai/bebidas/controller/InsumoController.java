package br.com.senai.bebidas.controller;

import br.com.senai.bebidas.dto.request.InsumoCreateRequest;
import br.com.senai.bebidas.dto.request.InsumoUpdateRequest;
import br.com.senai.bebidas.dto.response.InsumoResponse;
import br.com.senai.bebidas.service.InsumoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/insumos")
public class InsumoController {

    private final InsumoService insumoService;

    public InsumoController(InsumoService insumoService) {
        this.insumoService = insumoService;
    }

    @PostMapping
    public ResponseEntity<InsumoResponse> criar(@Valid @RequestBody InsumoCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(insumoService.criar(request));
    }

    @GetMapping
    public ResponseEntity<List<InsumoResponse>> listar(
            @RequestParam(name = "nome", required = false) String nome,
            @RequestParam(name = "ativo", required = false) String ativo) {
        return ResponseEntity.ok(insumoService.listar(nome, ativo));
    }

    @GetMapping("/{id}")
    public ResponseEntity<InsumoResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(insumoService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<InsumoResponse> atualizar(@PathVariable Long id,
                                                    @Valid @RequestBody InsumoUpdateRequest request) {
        return ResponseEntity.ok(insumoService.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirOuInativar(@PathVariable Long id) {
        insumoService.excluirOuInativar(id);
        return ResponseEntity.noContent().build();
    }
}
