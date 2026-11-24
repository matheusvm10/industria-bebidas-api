package br.com.senai.bebidas.controller;

import br.com.senai.bebidas.dto.request.SetorCreateRequest;
import br.com.senai.bebidas.dto.request.SetorUpdateRequest;
import br.com.senai.bebidas.dto.response.SetorResponse;
import br.com.senai.bebidas.service.SetorService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/setores")
public class SetorController {

    private final SetorService setorService;

    public SetorController(SetorService setorService) {
        this.setorService = setorService;
    }

    @PostMapping
    public ResponseEntity<SetorResponse> criar(@Valid @RequestBody SetorCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(setorService.criar(request));
    }

    @GetMapping
    public ResponseEntity<List<SetorResponse>> listar(
            @RequestParam(name = "ativo", required = false) String ativo) {
        return ResponseEntity.ok(setorService.listar(ativo));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SetorResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(setorService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SetorResponse> atualizar(@PathVariable Long id,
                                                   @Valid @RequestBody SetorUpdateRequest request) {
        return ResponseEntity.ok(setorService.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirOuInativar(@PathVariable Long id) {
        setorService.excluirOuInativar(id);
        return ResponseEntity.noContent().build();
    }
}
