package dev.aplication.sistemaCadastro.dto;

import dev.aplication.sistemaCadastro.model.StatusUsuario;

import java.time.LocalDateTime;

public record UsuarioResponseDTO(
        Integer id,
        String nome,
        String email,
        String telefone,
        String cpf,
        String rg,
        StatusUsuario status,
        Boolean ativo,
        LocalDateTime criadoEm,
        LocalDateTime atualizadoEm
) {
}