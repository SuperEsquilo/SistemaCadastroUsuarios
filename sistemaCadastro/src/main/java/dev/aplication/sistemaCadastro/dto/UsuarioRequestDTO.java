package dev.aplication.sistemaCadastro.dto;

import dev.aplication.sistemaCadastro.model.TipoUsuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.br.CPF;

public record UsuarioRequestDTO(
        @NotBlank(message = "Nome de usuário é obrigatório")
        @Size(min = 3, max = 120, message = "Nome deve ter no máximo 120 caracteres")
        String nome,

        @NotBlank(message = "E-mail de usuário é obrigatório")
        @Email(message = "Formato de e-mail é inválido")
        @Size(max = 120)
        String email,

        @NotBlank(message = "Telefone de usuário é obirgatório")
        @Size(max = 120)
        String telefone,

        @NotBlank(message = "CPF do usuário é obrigatório")
        @CPF(message = "Formato de CPF inválido")
        @Size(max = 11)
        String cpf,

        @NotBlank(message = "RG do usuário é obrigatório")
        @Size(max = 11)
        String rg,

        @NotBlank(message = "Senha é obrigatória")
        @Size(min = 6, max = 255, message = "Senha deve ter no mínimo 6 caracteres")
        String senha,

        TipoUsuario tipoUsuario
) {
}