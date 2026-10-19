package dev.aplication.sistemaCadastro.dto;

import dev.aplication.sistemaCadastro.model.RegistrarUsuario;
import dev.aplication.sistemaCadastro.model.TipoUsuario;
import dev.aplication.sistemaCadastro.model.Usuario;

public record RegistrarUsuarioResponseDTO(
        Integer id,
        String nome,
        String email,
        TipoUsuario tipoUsuario
) {
    public static RegistrarUsuarioResponseDTO fromEntity(RegistrarUsuario r) {
        Usuario u = r.getUsuario();
        return new RegistrarUsuarioResponseDTO(
                u.getId(),
                u.getNome(),
                r.getEmail(),
                u.getTipoUsuario()
        );
    }

    public static RegistrarUsuarioResponseDTO fromEntity(Usuario u) {
        return new RegistrarUsuarioResponseDTO(
                u.getId(),
                u.getNome(),
                u.getEmail(),
                u.getTipoUsuario()
        );
    }
}