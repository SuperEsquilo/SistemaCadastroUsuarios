package dev.aplication.sistemaCadastro.controller;

import dev.aplication.sistemaCadastro.dto.LoginRequestDTO;
import dev.aplication.sistemaCadastro.dto.LoginResponseDTO;
import dev.aplication.sistemaCadastro.dto.UsuarioRequestDTO;
import dev.aplication.sistemaCadastro.dto.UsuarioResponseDTO;
import dev.aplication.sistemaCadastro.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO dto) {
        LoginResponseDTO resposta = usuarioService.autenticar(dto);
        return ResponseEntity.ok(resposta);
    }

    @GetMapping
    public ResponseEntity<List<UsuarioResponseDTO>> listarTodos() {
        return ResponseEntity.ok(usuarioService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> buscarPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(usuarioService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> salvar(@Valid @RequestBody UsuarioRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.salvar(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> atualizar(
            @PathVariable Integer id,
            @Valid @RequestBody UsuarioRequestDTO dto) {
        return ResponseEntity.ok(usuarioService.atualizar(id, dto));
    }

    @PatchMapping("/{id}/ativar")
    public ResponseEntity<UsuarioResponseDTO> ativar(@PathVariable Integer id) {
        return ResponseEntity.ok(usuarioService.ativar(id));
    }

    @PatchMapping("/{id}/desativar")
    public ResponseEntity<UsuarioResponseDTO> desativar(@PathVariable Integer id) {
        return ResponseEntity.ok(usuarioService.desativar(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Integer id) {
        usuarioService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}