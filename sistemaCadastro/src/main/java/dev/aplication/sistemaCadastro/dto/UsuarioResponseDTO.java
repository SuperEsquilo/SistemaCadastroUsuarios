package dev.aplication.sistemaCadastro.dto;

import dev.aplication.sistemaCadastro.model.StatusUsuario;
import dev.aplication.sistemaCadastro.model.TipoUsuario;
import dev.aplication.sistemaCadastro.model.Usuario;

import java.time.LocalDateTime;

public record UsuarioResponseDTO(
        Integer id,
        String nome,
        String email,
        String telefone,
        String cpf,
        String rg,
        TipoUsuario tipoUsuario,
        StatusUsuario status,
        Boolean ativo,
        LocalDateTime criadoEm,
        LocalDateTime atualizadoEm
) {
    public static UsuarioResponseDTO fromEntity(Usuario u) {
        Boolean ativo = (u.getRegistrarUsuario() != null)
                ? u.getRegistrarUsuario().getAtivo()
                : (u.getStatus() == StatusUsuario.ATIVO);

        return new UsuarioResponseDTO(
                u.getId(),
                u.getNome(),
                u.getEmail(),
                u.getTelefone(),
                u.getCpf(),
                u.getRg(),
                u.getTipoUsuario(),
                u.getStatus(),
                ativo,
                u.getCriadoEm(),
                u.getAtualizadoEm()
        );
    }
}