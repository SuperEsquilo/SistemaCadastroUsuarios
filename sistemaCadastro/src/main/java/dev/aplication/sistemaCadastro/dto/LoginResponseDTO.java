package dev.aplication.sistemaCadastro.dto;

import dev.aplication.sistemaCadastro.model.StatusUsuario;
import dev.aplication.sistemaCadastro.model.TipoUsuario;

public record LoginResponseDTO(
        Integer id,
        String nome,
        String email,
        TipoUsuario tipoUsuario,
        StatusUsuario status,
        String mensagem
) {
    public LoginResponseDTO(Integer id, String nome, String email, TipoUsuario tipoUsuario) {
        this(id, nome, email, tipoUsuario, StatusUsuario.ATIVO, "Login realizado com sucesso!");
    }
}
