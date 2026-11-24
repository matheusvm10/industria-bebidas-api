package br.com.senai.bebidas.controller;

import br.com.senai.bebidas.dto.request.FornecedorCreateRequest;
import br.com.senai.bebidas.dto.request.FornecedorUpdateRequest;
import br.com.senai.bebidas.dto.response.FornecedorResponse;
import br.com.senai.bebidas.service.FornecedorService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fornecedores")
public class FornecedorController {

    private final FornecedorService fornecedorService;

    public FornecedorController(FornecedorService fornecedorService) {
        this.fornecedorService = fornecedorService;
    }

    @PostMapping
    public ResponseEntity<FornecedorResponse> criar(@Valid @RequestBody FornecedorCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(fornecedorService.criar(request));
    }

    @GetMapping
    public ResponseEntity<List<FornecedorResponse>> listar(
            @RequestParam(name = "ativo", required = false) String ativo) {
        return ResponseEntity.ok(fornecedorService.listar(ativo));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FornecedorResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(fornecedorService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FornecedorResponse> atualizar(@PathVariable Long id,
                                                        @Valid @RequestBody FornecedorUpdateRequest request) {
        return ResponseEntity.ok(fornecedorService.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirOuInativar(@PathVariable Long id) {
        fornecedorService.excluirOuInativar(id);
        return ResponseEntity.noContent().build();
    }
}
