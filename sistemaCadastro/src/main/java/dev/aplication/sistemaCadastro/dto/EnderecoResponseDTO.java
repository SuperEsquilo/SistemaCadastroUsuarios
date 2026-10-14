package dev.aplication.sistemaCadastro.dto;

import java.time.LocalDateTime;

public record EnderecoResponseDTO(
    Integer id,
    String rua,
    String numero,
    String complemento,
    String bairro,
    String cidade,
    String uf,
    String cep,
    LocalDateTime criadoEm,
    LocalDateTime atualizadoEm
) {
}