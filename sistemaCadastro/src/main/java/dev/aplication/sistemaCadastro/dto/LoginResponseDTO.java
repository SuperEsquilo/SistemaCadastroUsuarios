package dev.aplication.sistemaCadastro.dto;

import dev.aplication.sistemaCadastro.model.TipoUsuario;

public record LoginResponseDTO(
        Integer id,
        String nome,
        String email,
        TipoUsuario tipoUsuario
) {
}

