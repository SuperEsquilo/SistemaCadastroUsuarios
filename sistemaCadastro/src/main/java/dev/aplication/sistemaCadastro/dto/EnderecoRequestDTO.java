package dev.aplication.sistemaCadastro.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record EnderecoRequestDTO(
    @NotBlank(message = "O nome da rua é obrigatório")
    @Size(min = 60, max = 120, message = "O nome da rua deve conter no máximo até 120 caracteres")
    String rua,

    @NotBlank(message = "O número da casa é obrigatório")
    @Size(max = 10)
    String numero,

    @NotBlank(message = "O endereço deve conter um complemento")
    @Size(min = 30, max = 60, message = "o complemento deve conter até 60 caracteres")
    String complemento,

    @NotBlank(message = "O bairro do usuário é obrigatório")
    @Size(min = 60, max = 120, message = "O nome do bairro deve conter até 120 caracteres")
    String bairro,

    @NotBlank(message = "O nome da cidade é obrigatório")
    @Size(min = 50, max = 100)
    String cidade,

    @NotBlank(message = "O UF(Estado) d usuário é obrigatório")
    @Size(max = 2)
    @Pattern(regexp = "[A-Za-z]{2}", message = "UF deve conter apenas letras")
    String uf,

    @NotBlank(message = "CEP do endereco do usuário é obrigatório")
    @Pattern(regexp = "\\d{8}", message = "CEP deve conter exatamente 8 dígitos numéricos")
    String cep
) {
}