package dev.aplication.sistemaCadastro.controller;

import dev.aplication.sistemaCadastro.dto.AlterarSenhaResponseDTO;
import dev.aplication.sistemaCadastro.dto.RegistrarUsuarioRequestDTO;
import dev.aplication.sistemaCadastro.dto.RegistrarUsuarioResponseDTO;
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
        RegistrarUsuarioResponseDTO resposne = registrarUsuarioService.registrar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(resposne);
    }

    @PostMapping("/{usuarioId}/senha")
    public ResponseEntity<Void> alterarSenha(@PathVariable Integer usuarioId, @Valid @RequestBody AlterarSenhaResponseDTO dto) {
        return null;
    }
}