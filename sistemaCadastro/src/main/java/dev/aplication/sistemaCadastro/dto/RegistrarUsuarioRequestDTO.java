package dev.aplication.sistemaCadastro.dto;

import dev.aplication.sistemaCadastro.model.TipoUsuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegistrarUsuarioRequestDTO(
        @NotBlank(message = "Nome é obrigatório")
        @Size(min = 3, max = 120, message = "Nome deve ter entre 3 e 120 caracteres")
        String nome,

        @NotBlank(message = "E-mail é obrigatório")
        @Email(message = "Formato de e-mail inválido")
        @Size(max = 120, message = "E-mail deve ter no máximo 120 caracteres")
        String email,

        @NotBlank(message = "Telefone é obrigatório")
        @Size(max = 20, message = "Telefone deve ter no máximo 20 caracteres")
        String telefone,

        @NotBlank(message = "CPF é obrigatório")
        String cpf,

        @NotBlank(message = "RG é obrigatório")
        String rg,

        @NotBlank(message = "Senha é obrigatória")
        @Size(min = 6, max = 72, message = "Senha deve ter entre 6 e 72 caracteres")
        String senha,

        TipoUsuario tipoUsuario
) {
}