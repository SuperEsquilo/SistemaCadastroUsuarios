package dev.aplication.sistemaCadastro.dto;

import dev.aplication.sistemaCadastro.model.Endereco;

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
    public static EnderecoResponseDTO fromEntity(Endereco e) {
        return new EnderecoResponseDTO(
                e.getId(),
                e.getRua(),
                e.getNumero(),
                e.getComplemento(),
                e.getBairro(),
                e.getCidade(),
                e.getUf(),
                e.getCep(),
                e.getCriadoEm(),
                e.getAtualizadoEm()
        );
    }
}