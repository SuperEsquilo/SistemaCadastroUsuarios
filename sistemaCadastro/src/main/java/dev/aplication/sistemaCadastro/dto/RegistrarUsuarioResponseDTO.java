package dev.aplication.sistemaCadastro.dto;

import dev.aplication.sistemaCadastro.model.RegistrarUsuario;
import dev.aplication.sistemaCadastro.model.StatusUsuario;
import dev.aplication.sistemaCadastro.model.TipoUsuario;
import dev.aplication.sistemaCadastro.model.Usuario;

public record RegistrarUsuarioResponseDTO(
        Integer id,
        String nome,
        String email,
        TipoUsuario tipoUsuario,
        StatusUsuario status,
        Boolean ativo
) {
    public static RegistrarUsuarioResponseDTO fromEntity(RegistrarUsuario r) {
        Usuario u = r.getUsuario();
        return new RegistrarUsuarioResponseDTO(
                u.getId(),
                u.getNome(),
                r.getEmail(),
                u.getTipoUsuario(),
                u.getStatus(),
                r.getAtivo()
        );
    }

    public static RegistrarUsuarioResponseDTO fromEntity(Usuario u, RegistrarUsuario r) {
        return new RegistrarUsuarioResponseDTO(
                u.getId(),
                u.getNome(),
                u.getEmail(),
                u.getTipoUsuario(),
                u.getStatus(),
                r != null ? r.getAtivo() : (u.getStatus() == StatusUsuario.ATIVO)
        );
    }

    public static RegistrarUsuarioResponseDTO fromEntity(Usuario u) {
        return fromEntity(u, u.getRegistrarUsuario());
    }
}