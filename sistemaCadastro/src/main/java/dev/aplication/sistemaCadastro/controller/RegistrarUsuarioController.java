package dev.aplication.sistemaCadastro.controller;

import dev.aplication.sistemaCadastro.dto.*;
import dev.aplication.sistemaCadastro.service.RegistrarUsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class RegistrarUsuarioController {

    private final RegistrarUsuarioService registrarUsuarioService;

    public RegistrarUsuarioController(RegistrarUsuarioService registrarUsuarioService) {
        this.registrarUsuarioService = registrarUsuarioService;
    }

    @PostMapping("/registrar")
    public ResponseEntity<RegistrarUsuarioResponseDTO> registrar(@Valid @RequestBody RegistrarUsuarioRequestDTO dto) {
        RegistrarUsuarioResponseDTO response = registrarUsuarioService.registrar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO dto) {
        LoginResponseDTO response = registrarUsuarioService.autenticar(dto);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{usuarioId}/senha")
    public ResponseEntity<AlterarSenhaResponseDTO> alterarSenhaPost(
            @PathVariable Integer usuarioId,
            @Valid @RequestBody AlterarSenhaRequestDTO dto) {
        return ResponseEntity.ok(registrarUsuarioService.alterarSenha(usuarioId, dto));
    }

    @PatchMapping("/{usuarioId}/senha")
    public ResponseEntity<AlterarSenhaResponseDTO> alterarSenhaPatch(
            @PathVariable Integer usuarioId,
            @Valid @RequestBody AlterarSenhaRequestDTO dto) {
        return ResponseEntity.ok(registrarUsuarioService.alterarSenha(usuarioId, dto));
    }
}