package dev.aplication.sistemaCadastro.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record EnderecoRequestDTO(
        @NotBlank(message = "O nome da rua é obrigatório")
        @Size(min = 2, max = 120, message = "O nome da rua deve conter até 120 caracteres")
        String rua,

        @NotBlank(message = "O número da casa é obrigatório")
        @Size(max = 10, message = "O número deve ter no máximo 10 caracteres")
        String numero,

        @Size(max = 60, message = "O complemento deve conter até 60 caracteres")
        String complemento,

        @NotBlank(message = "O bairro do usuário é obrigatório")
        @Size(min = 2, max = 120, message = "O nome do bairro deve conter até 120 caracteres")
        String bairro,

        @NotBlank(message = "O nome da cidade é obrigatório")
        @Size(min = 2, max = 100, message = "O nome da cidade deve conter até 100 caracteres")
        String cidade,

        @NotBlank(message = "O UF (Estado) do usuário é obrigatório")
        @Pattern(regexp = "^[A-Za-z]{2}$", message = "UF deve conter exatamente 2 letras")
        String uf,

        @NotBlank(message = "CEP do endereço do usuário é obrigatório")
        @Pattern(regexp = "^\\d{8}$", message = "CEP deve conter exatamente 8 dígitos numéricos")
        String cep
) {
}