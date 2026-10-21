package dev.aplication.sistemaCadastro.dto;

import dev.aplication.sistemaCadastro.model.TipoUsuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UsuarioRequestDTO(
        @NotBlank(message = "Nome de usuário é obrigatório")
        @Size(min = 3, max = 120, message = "Nome deve ter entre 3 e 120 caracteres")
        String nome,

        @NotBlank(message = "E-mail de usuário é obrigatório")
        @Email(message = "Formato de e-mail é inválido")
        @Size(max = 120, message = "E-mail deve ter no máximo 120 caracteres")
        String email,

        @NotBlank(message = "Telefone de usuário é obrigatório")
        @Size(max = 20, message = "Telefone deve ter no máximo 20 caracteres")
        String telefone,

        @NotBlank(message = "CPF do usuário é obrigatório")
        String cpf,

        @NotBlank(message = "RG do usuário é obrigatório")
        String rg,

        String senha,

        TipoUsuario tipoUsuario
) {
}